package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbExecutor;
import org.beanmaker.v2.database.sql.DbQueryRetrieveData;
import org.beanmaker.v2.database.sql.DbQuerySetup;
import org.beanmaker.v2.database.sql.DbTransaction;

import org.beanmaker.v2.runtime.DbBeanLabel;
import org.beanmaker.v2.runtime.DbBeanLabelEditor;
import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.beanmaker.v2.util.Dates;
import org.beanmaker.v2.util.Strings;

import rodeo.password.pgencheck.PasswordMaker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    final String labelDataQuery;
    private final String idFromNameQuery;
    private final String idCheckQuery;
    private final String deleteUpdate;
    private final String autoLabelDeleteUpdate;

    public LabelHelper(String labelTable, String labelDataTable) {
        this(labelTable, labelDataTable, DEFAULT_AUTO_LABEL_NAME_PREFIX);
    }

    public LabelHelper(String labelTable, String labelDataTable, String labelAutoNamePrefix) {
        this.labelTable = labelTable;
        this.labelDataTable = labelDataTable;
        this.labelAutoNamePrefix = labelAutoNamePrefix;

        labelDataQuery   = "SELECT `data` FROM " + labelDataTable + " WHERE id_label=? AND id_language=?";
        idFromNameQuery  = "SELECT id FROM " + labelTable + " WHERE `name`=?";
        idCheckQuery     = "SELECT id FROM " + labelTable + " WHERE id=?";
        deleteUpdate     = "DELETE FROM " + labelTable + " WHERE id=?";
        autoLabelDeleteUpdate = "DELETE FROM " + labelTable + " WHERE id=? AND `name` LIKE ?";
    }

    public String get(DbExecutor dbExecutor, long id, DbBeanLanguage dbBeanLanguage, Object... parameters) {
        if (parameters == null || parameters.length == 0)
            return get(dbExecutor, id, dbBeanLanguage, Collections.emptyList());

        return get(dbExecutor, id, dbBeanLanguage, Arrays.asList(parameters));
    }

    public String get(DbExecutor dbExecutor, long id, DbBeanLanguage dbBeanLanguage, List<Object> parameters) {
        return processParameters(
                processResult(
                        dbExecutor.processQuery(
                                labelDataQuery,
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

    String processResult(String result, long id, DbBeanLanguage dbBeanLanguage) {
        return Objects.requireNonNull(
                result,
                "No data for label #" + id + " & language: " + dbBeanLanguage.getCapIso()
        );
    }

    DbQuerySetup setProcessingParameters(long id, DbBeanLanguage dbBeanLanguage) {
        return stat -> {
            stat.setLong(1, id);
            stat.setLong(2, dbBeanLanguage.getId());
        };
    }

    DbQueryRetrieveData<String> getResult() {
        return rs -> {
            if (rs.next())
                return rs.getString(1);

            return null;
        };
    }

    public String get(DbExecutor dbExecutor, long id, DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters) {
        return processParameters(
                processResult(
                        dbExecutor.processQuery(
                                labelDataQuery,
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

    public boolean hasDataFor(DbExecutor dbExecutor, long id, DbBeanLanguage dbBeanLanguage) {
        return dbExecutor.processQuery(
                labelDataQuery,
                setProcessingParameters(id, dbBeanLanguage),
                ResultSet::next
        );
    }

    public boolean isIdOK(DbExecutor dbExecutor, long id) {
        return dbExecutor.processQuery(
                idCheckQuery,
                stat -> stat.setLong(1, id),
                ResultSet::next
        );
    }

    public boolean isNameOK(DbExecutor dbExecutor, String name) {
        return dbExecutor.processQuery(
                idFromNameQuery,
                stat -> stat.setString(1, name),
                ResultSet::next
        );
    }

    public String get(DbExecutor dbExecutor, String name, DbBeanLanguage dbBeanLanguage, Object... parameters) {
        return get(dbExecutor, getLabelID(dbExecutor, name), dbBeanLanguage, parameters);
    }

    public String get(DbExecutor dbExecutor, String name, DbBeanLanguage dbBeanLanguage, List<Object> parameters) {
        return get(dbExecutor, getLabelID(dbExecutor, name), dbBeanLanguage, parameters);
    }

    public String get(DbExecutor dbExecutor, String name, DbBeanLanguage dbBeanLanguage, Map<String, Object> parameters) {
        return get(dbExecutor, getLabelID(dbExecutor, name), dbBeanLanguage, parameters);
    }

    public long getLabelID(DbExecutor dbExecutor, String name) {
        return dbExecutor.processQuery(
                idFromNameQuery,
                stat -> stat.setString(1, name),
                getIdOrThrow(name)
        );
    }

    private DbQueryRetrieveData<Long> getIdOrThrow(String name) {
        return rs -> {
            if (rs.next())
                return rs.getLong(1);

            throw new IllegalArgumentException("No label with name: " + name);
        };
    }

    public void updateValues(DbTransaction transaction, DbBeanLabel label, Map<DbBeanLanguage, String> values) {
        for (var value: values.entrySet())
            transaction.processUpdate(
                    "INSERT INTO " + labelDataTable + " (id_label, id_language, data) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, label.getId());
                        stat.setLong(2, value.getKey().getId());
                        stat.setString(3, value.getValue());
                    }
            );
    }

    public long createLabel(DbTransaction transaction, Map<DbBeanLanguage, String> values) {
        long id = transaction.createRecord(
                "INSERT INTO " + labelTable + " (name) VALUES (?)",
                stat -> stat.setString(1, createUniqueLabelName())
        );
        for (var value: values.entrySet()) {
            transaction.processUpdate(
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

    public void quickUpdate(DbExecutor dbExecutor, DbBeanLabel label, DbBeanLanguage language, String value) {
        int count = dbExecutor.processUpdate(
                "UPDATE " + labelDataTable + " SET data=? WHERE id_label=? AND id_language=?",
                stat -> {
                    stat.setString(1, value);
                    stat.setLong(2, label.getId());
                    stat.setLong(3, language.getId());
                }
        );

        if (count == 0)
            dbExecutor.processUpdate(
                    "INSERT INTO " + labelDataTable + " (id_label, id_language, data) VALUES (?, ?, ?)",
                    stat -> {
                        stat.setLong(1, label.getId());
                        stat.setLong(2, language.getId());
                        stat.setString(3, value);
                    }
            );
    }

    public long quickCreate(DbTransaction transaction, DbBeanLanguage language, String value) {
        long id = transaction.createRecord(
                "INSERT INTO " + labelTable + " (name) VALUES (?)",
                stat -> stat.setString(1, createUniqueLabelName())
        );
        transaction.processUpdate(
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

    public long duplicate(DbTransaction transaction, DbBeanLabel label, List<DbBeanLanguage> languages) {
        return createLabel(transaction, extractContent(label, languages));
    }

    public Map<DbBeanLanguage, String> extractContent(DbBeanLabel label, List<DbBeanLanguage> languages) {
        var map = new HashMap<DbBeanLanguage, String>();
        for (var language: languages) {
            if (label.hasDataFor(language))
                map.put(language, label.get(language));
        }
        return map;
    }

    public void cacheLabelsFromDB(
            DbExecutor dbExecutor,
            DbBeanLabelEditor labelEditor,
            List<DbBeanLanguage> languages,
            Map<DbBeanLanguage,String> cache)
    {
        if (labelEditor.getId() == 0)
            throw new IllegalArgumentException("Cannot cache labels for a record not yet in the database.");

        cache.clear();

        dbExecutor.processQueries(
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

    public void updateTextValues(DbTransaction transaction, long idLabel, Map<DbBeanLanguage, String> values) {
        transaction.processUpdate(
                "DELETE FROM " + labelDataTable + " WHERE id_label=?",
                stat -> stat.setLong(1, idLabel)
        );

        if (!values.isEmpty()) {
            transaction.processUpdates(
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

    private static final String UNIQUE_LABEL_TEST_REQUEST = """
            SELECT %s.id_label FROM %s
            INNER JOIN %s ON %s.id_label=%s.%s
            WHERE id_language=? AND %s.id_label <> ? AND `data`=?""";

    public boolean labelExistsInContext(
            DbExecutor dbExecutor,
            String table,
            String field,
            long idLabel,
            DbBeanLanguage language,
            String text)
    {
        return dbExecutor.processQuery(
                UNIQUE_LABEL_TEST_REQUEST.formatted(
                        labelDataTable,
                        table,
                        labelDataTable,
                        labelDataTable,
                        table,
                        field,
                        labelDataTable
                ),
                stat -> {
                    stat.setLong(1, language.getId());
                    stat.setLong(2, idLabel);
                    stat.setString(3, text);
                },
                ResultSet::next
        );
    }

    public void deleteLabel(DbExecutor dbExecutor, long id) {
        dbExecutor.processUpdate(deleteUpdate, stat -> stat.setLong(1, id));
    }

    public void deleteAutoLabel(DbExecutor dbExecutor, long id) {
        dbExecutor.processUpdate(
                autoLabelDeleteUpdate,
                stat -> {
                    stat.setLong(1, id);
                    stat.setString(2, labelAutoNamePrefix + "%");
                }
        );
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
