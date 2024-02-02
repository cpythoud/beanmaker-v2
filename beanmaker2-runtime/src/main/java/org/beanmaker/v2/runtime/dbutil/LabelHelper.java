package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLabel;
import org.beanmaker.v2.runtime.DbBeanLabelEditor;
import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.beanmaker.v2.util.Dates;
import org.beanmaker.v2.util.Strings;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQueryRetrieveData;
import org.dbbeans.sql.DBQuerySetup;
import org.dbbeans.sql.DBTransaction;

import rodeo.password.pgencheck.PasswordMaker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.function.Function;

import static rodeo.password.pgencheck.CharacterGroups.DIGITS;
import static rodeo.password.pgencheck.CharacterGroups.LOWER_CASE;
import static rodeo.password.pgencheck.CharacterGroups.UPPER_CASE;

public class LabelHelper {

    private static final String DEFAULT_AUTO_LABEL_NAME_PREFIX = "XXX-";

    private static final PasswordMaker LABEL_CODE_EXTRA_CHARS = PasswordMaker
            .factory()
            .addCharGroup(LOWER_CASE, 1)
            .addCharGroup(UPPER_CASE, 1)
            .addCharGroup(DIGITS, 1)
            .setLength(6)
            .create();

    private final String labelTable;
    private final String labelDataTable;
    private final String labelAutoNamePrefix;

    private final String idBasedDataQuery;
    private final String idFromNameQuery;
    private final String labelDataQuery;

    public LabelHelper(String labelTable, String labelDataTable) {
        this(labelTable, labelDataTable, DEFAULT_AUTO_LABEL_NAME_PREFIX);
    }

    public LabelHelper(String labelTable, String labelDataTable, String labelAutoNamePrefix) {
        this.labelTable = labelTable;
        this.labelDataTable = labelDataTable;
        this.labelAutoNamePrefix = labelAutoNamePrefix;

        idBasedDataQuery = "SELECT `data` FROM " + labelDataTable + " WHERE id_label=? AND id_language=?";
        idFromNameQuery = "SELECT id FROM " + labelTable + " WHERE `name`=?";
        labelDataQuery = "SELECT data FROM " + labelDataTable + " WHERE id_label=? AND id_language=?";
    }

    public String get(DBAccess dbAccess, long id, DbBeanLanguage dbBeanLanguage, Object... parameters) {
        if (parameters == null || parameters.length == 0)
            return get(dbAccess, id, dbBeanLanguage, Collections.emptyList());

        return get(dbAccess, id, dbBeanLanguage, Arrays.asList(parameters));
    }

    public String get(DBAccess dbAccess, long id, DbBeanLanguage dbBeanLanguage, List<Object> parameters) {
        return processParameters(
                processResult(
                        dbAccess.processQuery(
                                idBasedDataQuery,
                                setProcessingParameters(id, dbBeanLanguage),
                                getResult()),
                        id,
                        dbBeanLanguage
                ),
                parameters
        );
    }

    public String processParameters(String text, Object... parameters) {
        if (parameters == null || parameters.length == 0)
            return text;

        return processParameters(text, Arrays.asList(parameters));
    }

    public String processParameters(String text, List<Object> parameters) {
        if (text == null)
            return null;
        if (parameters.isEmpty())
            return text;

        if (parameters.size() > 9)
            throw new IllegalArgumentException("Too many parameters: " + parameters.size() + ", max = 9.");

        int index = 0;
        for (Object parameter: parameters) {
            ++index;
            text = text.replaceAll("#" + index, parameter.toString());
        }

        return text;
    }

    private String processResult(String result, long id, DbBeanLanguage dbBeanLanguage) {
        return Objects.requireNonNull(result, "No data for label #" + id + " & language: " + dbBeanLanguage.getCapIso());
    }

    private DBQuerySetup setProcessingParameters(long id, DbBeanLanguage dbBeanLanguage) {
        return stat -> {
            stat.setLong(1, id);
            stat.setLong(2, dbBeanLanguage.getId());
        };
    }

    private DBQueryRetrieveData<String> getResult() {
        return rs -> {
            if (rs.next())
                return rs.getString(1);

            return null;
        };
    }

    public String get(DBAccess dbAccess, long id, DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters) {
        return processParameters(
                processResult(
                        dbAccess.processQuery(
                                idBasedDataQuery,
                                setProcessingParameters(id, dbBeanLanguage),
                                getResult()),
                        id,
                        dbBeanLanguage
                ),
                parameters
        );
    }

    public String processParameters(String text, Map<String, Object> parameters) {
        if (text == null)
            return null;
        if (parameters.isEmpty())
            return text;

        return Strings.replaceWithParameters(text, parameters);
    }

    public boolean hasDataFor(DBAccess dbAccess, long id, DbBeanLanguage dbBeanLanguage) {
        return dbAccess.processQuery(
                idBasedDataQuery,
                setProcessingParameters(id, dbBeanLanguage),
                ResultSet::next
        );
    }

    public String get(DBTransaction transaction, long id, DbBeanLanguage dbBeanLanguage, Object... parameters) {
        if (parameters == null || parameters.length == 0)
            return get(transaction, id, dbBeanLanguage, Collections.emptyList());

        return get(transaction, id, dbBeanLanguage, Arrays.asList(parameters));
    }

    public String get(DBTransaction transaction, long id, DbBeanLanguage dbBeanLanguage, List<Object> parameters) {
        return processParameters(
                processResult(
                        transaction.addQuery(
                                idBasedDataQuery,
                                setProcessingParameters(id, dbBeanLanguage),
                                getResult()),
                        id,
                        dbBeanLanguage
                ),
                parameters
        );
    }

    public String get(DBTransaction transaction, long id, DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters) {
        return processParameters(
                processResult(
                        transaction.addQuery(
                                idBasedDataQuery,
                                setProcessingParameters(id, dbBeanLanguage),
                                getResult()),
                        id,
                        dbBeanLanguage
                ),
                parameters
        );
    }

    public boolean hasDataFor(DBTransaction transaction, long id, DbBeanLanguage dbBeanLanguage) {
        return transaction.addQuery(
                idBasedDataQuery,
                setProcessingParameters(id, dbBeanLanguage),
                ResultSet::next
        );
    }

    public boolean isNameOK(DBAccess dbAccess, String name) {
        return dbAccess.processQuery(
                idFromNameQuery,
                stat -> stat.setString(1, name),
                ResultSet::next
        );
    }

    public boolean isNameOK(DBTransaction transaction, String name) {
        return transaction.addQuery(
                idFromNameQuery,
                stat -> stat.setString(1, name),
                ResultSet::next
        );
    }

    public String get(DBAccess dbAccess, String name, DbBeanLanguage dbBeanLanguage, Object... parameters) {
        return get(dbAccess, getLabelID(dbAccess, name), dbBeanLanguage, parameters);
    }

    public String get(DBAccess dbAccess, String name, DbBeanLanguage dbBeanLanguage, List<Object> parameters) {
        return get(dbAccess, getLabelID(dbAccess, name), dbBeanLanguage, parameters);
    }

    public String get(DBAccess dbAccess, String name, DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters) {
        return get(dbAccess, getLabelID(dbAccess, name), dbBeanLanguage, parameters);
    }

    public long getLabelID(DBAccess dbAccess, String name) {
        return dbAccess.processQuery(
                idFromNameQuery,
                stat -> stat.setString(1, name),
                getIdOrThrow(name)
        );
    }

    private DBQueryRetrieveData<Long> getIdOrThrow(String name) {
        return rs -> {
            if (rs.next())
                return rs.getLong(1);

            throw new IllegalArgumentException("No label with name: " + name);
        };
    }

    public String get(DBTransaction transaction, String name, DbBeanLanguage dbBeanLanguage, Object... parameters) {
        return get(transaction, getLabelID(transaction, name), dbBeanLanguage, parameters);
    }

    public String get(DBTransaction transaction, String name, DbBeanLanguage dbBeanLanguage, List<Object> parameters) {
        return get(transaction, getLabelID(transaction, name), dbBeanLanguage, parameters);
    }

    public String get(DBTransaction transaction, String name, DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters) {
        return get(transaction, getLabelID(transaction, name), dbBeanLanguage, parameters);
    }

    public long getLabelID(DBTransaction transaction, String name) {
        return transaction.addQuery(
                idFromNameQuery,
                stat -> stat.setString(1, name),
                getIdOrThrow(name)
        );
    }

    public void updateValues(DBTransaction transaction, DbBeanLabel label, Map<DbBeanLanguage, String> values) {
        for (var value: values.entrySet())
            transaction.addUpdate(
                    "INSERT INTO " + labelDataTable + " (id_label, id_language, data) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, label.getId());
                        stat.setLong(2, value.getKey().getId());
                        stat.setString(3, value.getValue());
                    }
            );
    }

    public long createLabel(DBTransaction transaction, Map<DbBeanLanguage, String> values) {
        long id = transaction.addRecordCreation(
                "INSERT INTO " + labelTable + " (name) VALUES (?)",
                stat -> {
                    stat.setString(1, createUniqueLabelName());
                }
        );
        for (var value: values.entrySet()) {
            transaction.addUpdate(
                    "INSERT INTO " + labelDataTable + " (id_label, id_language, data) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, id);
                        stat.setLong(2, value.getKey().getId());
                        stat.setString(3, value.getValue());
                    }
            );
        }
        return id;
    }

    public void quickUpdate(DBAccess dbAccess, DbBeanLabel label, DbBeanLanguage language, String value) {
        int count = dbAccess.processUpdate(
                "UPDATE " + labelDataTable + " SET data=? WHERE id_label=? AND id_language=?",
                stat -> {
                    stat.setString(1, value);
                    stat.setLong(2, label.getId());
                    stat.setLong(3, language.getId());
                }
        );

        if (count == 0)
            dbAccess.processUpdate(
                    "INSERT INTO " + labelDataTable + " (id_label, id_language, data) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, label.getId());
                        stat.setLong(2, language.getId());
                        stat.setString(3, value);
                    }
            );
    }

    public void quickUpdate(DBTransaction transaction, DbBeanLabel label, DbBeanLanguage language, String value) {
        int count = transaction.addUpdate(
                "UPDATE " + labelDataTable + " SET data=? WHERE id_label=? AND id_language=?",
                stat -> {
                    stat.setString(1, value);
                    stat.setLong(2, label.getId());
                    stat.setLong(3, language.getId());
                }
        );

        if (count == 0)
            transaction.addUpdate(
                    "INSERT INTO " + labelDataTable + " (id_label, id_language, data) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, label.getId());
                        stat.setLong(2, language.getId());
                        stat.setString(3, value);
                    }
            );
    }

    public long quickCreate(DBTransaction transaction, DbBeanLanguage language, String value) {
        long id = transaction.addRecordCreation(
                "INSERT INTO " + labelTable + " (name) VALUES (?)",
                stat -> {
                    stat.setString(1, createUniqueLabelName());
                }
        );
        transaction.addUpdate(
                "INSERT INTO " + labelDataTable + " (id_label, id_language, data) VALUES (?, ?, ?)",
                stat -> {
                    stat.setLong(1, id);
                    stat.setLong(2, language.getId());
                    stat.setString(3, value);
                }
        );
        return id;
    }

    public String createUniqueLabelName() {
        return labelAutoNamePrefix + Dates.getMeaningfulTimeStamp() + "-" + LABEL_CODE_EXTRA_CHARS.create();
    }

    public void cacheLabelsFromDB(
            DBAccess dbAccess,
            DbBeanLabelEditor labelEditor,
            List<DbBeanLanguage> languages,
            Map<DbBeanLanguage,String> cache)
    {
        if (labelEditor.getId() == 0)
            throw new IllegalArgumentException("Cannot cache labels for a record not yet in the database.");

        cache.clear();

        dbAccess.processQueries(
                labelDataQuery,
                stat -> {
                    updateCache(stat, labelEditor, languages, cache);
                });
    }

    public void cacheLabelsFromDB(
            DBTransaction transaction,
            DbBeanLabelEditor labelEditor,
            List<DbBeanLanguage> languages,
            Map<DbBeanLanguage,String> cache)
    {
        if (labelEditor.getId() == 0)
            throw new IllegalArgumentException("Cannot cache labels for a record not yet in the database.");

        cache.clear();

        transaction.addQueries(
                labelDataQuery,
                stat -> {
                    updateCache(stat, labelEditor, languages, cache);
                });
    }

    private void updateCache(
            PreparedStatement stat,
            DbBeanLabelEditor labelEditor,
            List<DbBeanLanguage> languages,
            Map<DbBeanLanguage,String> cache)
            throws SQLException
    {
        stat.setLong(1, labelEditor.getId());
        for (var language: languages) {
            stat.setLong(2, language.getId());
            ResultSet rs = stat.executeQuery();
            if (rs.next())
                cache.put(language, rs.getString(1));
        }
    }

    public void updateTextValues(DBTransaction transaction, long idLabel, Map<DbBeanLanguage, String> values) {
        transaction.addUpdate(
                "DELETE FROM " + labelDataTable + " WHERE id_label=?",
                stat -> stat.setLong(1, idLabel)
        );

        if (!values.isEmpty()) {
            transaction.addUpdates(
                    "INSERT INTO " + labelDataTable + " (id_label, id_language, `data`) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, idLabel);
                        for (var language : values.keySet()) {
                            stat.setLong(2, language.getId());
                            stat.setString(3, values.get(language));
                            stat.executeUpdate();
                        }
                    }
            );
        }
    }

    public static String getJavascriptLabelMap(
            String objectName,
            List<DbBeanLanguage> languages,
            List<DbBeanLabel> labels)
    {
        if (objectName == null || objectName.isEmpty())
            throw new IllegalArgumentException("Javascript object name cannot be null or empty");
        if (languages.isEmpty())
            throw new IllegalArgumentException("Language list cannot be empty");
        if (labels.isEmpty())
            throw new IllegalArgumentException("Label list cannot be empty");

        var javascript = new StringBuilder();
        javascript.append(objectName).append(" = \n{");

        for (var label: labels) {
            javascript.append("\n    \"").append(label.getName()).append("\" : {");
            for (var language: languages) {
                javascript.append("\n        \"").append(language.getIso()).append("\" : \"")
                        .append(label.get(language)).append("\",");
            }
            javascript.deleteCharAt(javascript.length() - 1);
            javascript.append("\n    },");
        }
        javascript.deleteCharAt(javascript.length() - 1);

        javascript.append("\n};");
        return javascript.toString();
    }

    public static String getJavascriptLabelMap(
            String objectName,
            List<DbBeanLanguage> languages,
            Function<String, DbBeanLabel> nameToLabel,
            List<String> labelNames)
    {
        return getJavascriptLabelMap(objectName, languages, labelNames.stream().map(nameToLabel).toList());
    }

    public static String getJavascriptLabelMap(
            String objectName,
            List<DbBeanLanguage> languages,
            Function<String, DbBeanLabel> nameToLabel,
            String... labelNames)
    {
        return getJavascriptLabelMap(objectName, languages, nameToLabel, Arrays.asList(labelNames));
    }

}
