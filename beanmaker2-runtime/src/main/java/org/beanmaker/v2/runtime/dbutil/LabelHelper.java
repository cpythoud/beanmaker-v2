package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.beanmaker.v2.util.Strings;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBQueryRetrieveData;
import org.dbbeans.sql.DBQuerySetup;
import org.dbbeans.sql.DBTransaction;

import java.sql.ResultSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LabelHelper {

    private final String idBasedDataQuery;


    public LabelHelper(String labelDataTable) {
        idBasedDataQuery = "SELECT `data` FROM " + labelDataTable + " WHERE id_label=? AND id_language=?";
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

    private String processParameters(String text, List<Object> parameters) {
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

    private String processParameters(String text, Map<String, Object> parameters) {
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

    public boolean hasDataFor(DBTransaction transaction, long id, DbBeanLanguage dbBeanLanguage) {
        return transaction.addQuery(
                idBasedDataQuery,
                setProcessingParameters(id, dbBeanLanguage),
                ResultSet::next
        );
    }

}
