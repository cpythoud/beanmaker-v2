package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbExecutor;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.beanmaker.v2.util.Strings;


public class CascadingLabelHelper extends LabelHelper {

    public CascadingLabelHelper(String labelTable, String labelDataTable) {
        super(labelTable, labelDataTable);
    }

    public String getCascading(
            DbExecutor dbExecutor,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        if (parameters.hasMapBasedParameters()) {
            return processParameters(
                    processResult(
                            getLabelText(dbExecutor, id, language, parameters),
                            id,
                            language
                    ),
                    parameters.getParameterMap()
            );
        }

        return processParameters(
                processResult(
                        getLabelText(dbExecutor, id, language, parameters),
                        id,
                        language
                ),
                parameters.getParameterList()
        );
    }

    private String getLabelText(
            DbExecutor dbExecutor,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        String labelText = dbExecutor.processQuery(
                labelDataQuery,
                setProcessingParameters(id, language),
                getResult());

        if (Strings.isEmpty(labelText)) {
            if (!language.isBareLanguage()) {
                labelText = dbExecutor.processQuery(
                        labelDataQuery,
                        setProcessingParameters(id, language.getBareLanguage()),
                        getResult());
            }
            if (Strings.isEmpty(labelText)) {
                var defaultLanguage = parameters.getDefaultLanguage();
                if (!language.isDefaultLanguage() && defaultLanguage != null) {
                    labelText = dbExecutor.processQuery(
                            labelDataQuery,
                            setProcessingParameters(id, defaultLanguage),
                            getResult());
                }
                if (Strings.isEmpty(labelText)) {
                    labelText = parameters.getDefaultValue();
                }
            }
        }

        return labelText;
    }

    public boolean hasCascadingDataFor(DbExecutor dbExecutor, long id, DbBeanLanguage dbBeanLanguage) {
        return hasCascadingDataFor(dbExecutor, id, dbBeanLanguage, null);
    }

    public boolean hasCascadingDataFor(
            DbExecutor dbExecutor,
            long id,
            DbBeanLanguage language,
            DbBeanLanguage defaultLanguage)
    {
        return dbExecutor.processQuery(
                labelDataQuery,
                setProcessingParameters(id, language),
                rs -> {
                    if (rs.next())
                        return true;
                    if (language.isBareLanguage()) {
                        if (!language.isDefaultLanguage() && defaultLanguage != null)
                            return hasCascadingDataFor(dbExecutor, id, defaultLanguage);
                        return false;
                    }
                    return hasCascadingDataFor(dbExecutor, id, language.getBareLanguage(), defaultLanguage);
                }
        );
    }

    /*public boolean hasCascadingDataFor(DbTransaction transaction, long id, DbBeanLanguage dbBeanLanguage) {
        return transaction.processQuery(
                labelDataQuery,
                setProcessingParameters(id, dbBeanLanguage),
                rs -> {
                    if (rs.next())
                        return true;
                    if (dbBeanLanguage.isBareLanguage())
                        return false;
                    return hasDataFor(transaction, id, dbBeanLanguage.getBareLanguage());
                }
        );
    }*/

    public String getCascading(
            DbExecutor dbExecutor,
            String name,
            DbBeanLanguage dbBeanLanguage,
            CascadingLabelHelperParameters parameters)
    {
        return getCascading(dbExecutor, getLabelID(dbExecutor, name), dbBeanLanguage, parameters);
    }

    /*public String getCascading(
            DbTransaction transaction,
            String name,
            DbBeanLanguage dbBeanLanguage,
            CascadingLabelHelperParameters parameters)
    {
        return getCascading(transaction, getLabelID(transaction, name), dbBeanLanguage, parameters);
    }*/

}
