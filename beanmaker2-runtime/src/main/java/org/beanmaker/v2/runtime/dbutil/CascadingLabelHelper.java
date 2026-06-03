package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbAccess;
import org.beanmaker.v2.database.sql.DbTransaction;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.beanmaker.v2.util.Strings;


public class CascadingLabelHelper extends LabelHelper {

    public CascadingLabelHelper(String labelTable, String labelDataTable) {
        super(labelTable, labelDataTable);
    }

    public String getCascading(
            DbAccess dbAccess,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        if (parameters.hasMapBasedParameters()) {
            return processParameters(
                    processResult(
                            getLabelText(dbAccess, id, language, parameters),
                            id,
                            language
                    ),
                    parameters.getParameterMap()
            );
        }

        return processParameters(
                processResult(
                        getLabelText(dbAccess, id, language, parameters),
                        id,
                        language
                ),
                parameters.getParameterList()
        );
    }

    private String getLabelText(
            DbAccess dbAccess,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        String labelText = dbAccess.processQuery(
                labelDataQuery,
                setProcessingParameters(id, language),
                getResult());

        if (Strings.isEmpty(labelText)) {
            if (!language.isBareLanguage()) {
                labelText = dbAccess.processQuery(
                        labelDataQuery,
                        setProcessingParameters(id, language.getBareLanguage()),
                        getResult());
            }
            if (Strings.isEmpty(labelText)) {
                var defaultLanguage = parameters.getDefaultLanguage();
                if (!language.isDefaultLanguage() && defaultLanguage != null) {
                    labelText = dbAccess.processQuery(
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

    public boolean hasCascadingDataFor(DbAccess dbAccess, long id, DbBeanLanguage dbBeanLanguage) {
        return hasCascadingDataFor(dbAccess, id, dbBeanLanguage, null);
    }

    public boolean hasCascadingDataFor(
            DbAccess dbAccess,
            long id,
            DbBeanLanguage language,
            DbBeanLanguage defaultLanguage)
    {
        return dbAccess.processQuery(
                labelDataQuery,
                setProcessingParameters(id, language),
                rs -> {
                    if (rs.next())
                        return true;
                    if (language.isBareLanguage()) {
                        if (!language.isDefaultLanguage() && defaultLanguage != null)
                            return hasCascadingDataFor(dbAccess, id, defaultLanguage);
                        return false;
                    }
                    return hasCascadingDataFor(dbAccess, id, language.getBareLanguage(), defaultLanguage);
                }
        );
    }

    public String getCascading(
            DbTransaction transaction,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        if (parameters.hasMapBasedParameters()) {
            return processParameters(
                    processResult(
                            getLabelText(transaction, id, language, parameters),
                            id,
                            language
                    ),
                    parameters.getParameterMap()
            );
        }

        return processParameters(
                processResult(
                        getLabelText(transaction, id, language, parameters),
                        id,
                        language
                ),
                parameters.getParameterList()
        );
    }

    private String getLabelText(
            DbTransaction transaction,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        String labelText = transaction.addQuery(
                labelDataQuery,
                setProcessingParameters(id, language),
                getResult());

        if (Strings.isEmpty(labelText)) {
            if (!language.isBareLanguage()) {
                labelText = transaction.addQuery(
                        labelDataQuery,
                        setProcessingParameters(id, language.getBareLanguage()),
                        getResult());
            }
            if (Strings.isEmpty(labelText)) {
                var defaultLanguage = parameters.getDefaultLanguage();
                if (!language.isDefaultLanguage() && defaultLanguage != null) {
                    labelText = transaction.addQuery(
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

    public boolean hasCascadingDataFor(DbTransaction transaction, long id, DbBeanLanguage dbBeanLanguage) {
        return transaction.addQuery(
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
    }

    public String getCascading(
            DbAccess dbAccess,
            String name,
            DbBeanLanguage dbBeanLanguage,
            CascadingLabelHelperParameters parameters)
    {
        return getCascading(dbAccess, getLabelID(dbAccess, name), dbBeanLanguage, parameters);
    }

    public String getCascading(
            DbTransaction transaction,
            String name,
            DbBeanLanguage dbBeanLanguage,
            CascadingLabelHelperParameters parameters)
    {
        return getCascading(transaction, getLabelID(transaction, name), dbBeanLanguage, parameters);
    }

}
