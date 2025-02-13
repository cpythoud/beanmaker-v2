package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanLanguage;

import org.beanmaker.v2.util.Strings;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

public class CascadingLabelHelper extends LabelHelper {

    public CascadingLabelHelper(String labelTable, String labelDataTable) {
        super(labelTable, labelDataTable);
    }

    public String getCascading(
            DBAccess dbAccess,
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
            DBAccess dbAccess,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        String labelText = dbAccess.processQuery(
                idBasedDataQuery,
                setProcessingParameters(id, language),
                getResult());

        if (Strings.isEmpty(labelText)) {
            if (!language.isBareLanguage()) {
                labelText = dbAccess.processQuery(
                        idBasedDataQuery,
                        setProcessingParameters(id, language.getBareLanguage()),
                        getResult());
            }
            if (Strings.isEmpty(labelText)) {
                var defaultLanguage = parameters.getDefaultLanguage();
                if (!language.isDefaultLanguage() && defaultLanguage != null) {
                    labelText = dbAccess.processQuery(
                            idBasedDataQuery,
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

    public boolean hasCascadingDataFor(DBAccess dbAccess, long id, DbBeanLanguage dbBeanLanguage) {
        return hasCascadingDataFor(dbAccess, id, dbBeanLanguage, null);
    }

    public boolean hasCascadingDataFor(
            DBAccess dbAccess,
            long id,
            DbBeanLanguage language,
            DbBeanLanguage defaultLanguage)
    {
        return dbAccess.processQuery(
                idBasedDataQuery,
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
            DBTransaction transaction,
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
            DBTransaction transaction,
            long id,
            DbBeanLanguage language,
            CascadingLabelHelperParameters parameters)
    {
        String labelText = transaction.addQuery(
                idBasedDataQuery,
                setProcessingParameters(id, language),
                getResult());

        if (Strings.isEmpty(labelText)) {
            if (!language.isBareLanguage()) {
                labelText = transaction.addQuery(
                        idBasedDataQuery,
                        setProcessingParameters(id, language.getBareLanguage()),
                        getResult());
            }
            if (Strings.isEmpty(labelText)) {
                var defaultLanguage = parameters.getDefaultLanguage();
                if (!language.isDefaultLanguage() && defaultLanguage != null) {
                    labelText = transaction.addQuery(
                            idBasedDataQuery,
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

    public boolean hasCascadingDataFor(DBTransaction transaction, long id, DbBeanLanguage dbBeanLanguage) {
        return transaction.addQuery(
                idBasedDataQuery,
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
            DBAccess dbAccess,
            String name,
            DbBeanLanguage dbBeanLanguage,
            CascadingLabelHelperParameters parameters)
    {
        return getCascading(dbAccess, getLabelID(dbAccess, name), dbBeanLanguage, parameters);
    }

    public String getCascading(
            DBTransaction transaction,
            String name,
            DbBeanLanguage dbBeanLanguage,
            CascadingLabelHelperParameters parameters)
    {
        return getCascading(transaction, getLabelID(transaction, name), dbBeanLanguage, parameters);
    }

}
