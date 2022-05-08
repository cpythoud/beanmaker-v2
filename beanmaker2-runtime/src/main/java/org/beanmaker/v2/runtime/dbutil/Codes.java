package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanEditor;
import org.beanmaker.v2.runtime.DbBeanInterface;
import org.beanmaker.v2.runtime.DbBeanParameters;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

import java.util.Optional;
import java.util.regex.Pattern;

public final class Codes {

    private static final Pattern VALIDATION_PATTERN = Pattern.compile("[\\w+-]{4,20}");

    private Codes() { }

    public static boolean validateFormat(final String code) {
        return VALIDATION_PATTERN.matcher(code).matches();
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            Class<? extends DbBeanInterface> beanClass,
            DbBeanParameters parameters,
            String code,
            DBAccess dbAccess)
    {
        return getBean(beanClass, parameters, "code", code, dbAccess);
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            Class<? extends DbBeanInterface> beanClass,
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBAccess dbAccess)
    {
        return SingleElements.getBean(
                getQuery(parameters, codeField),
                stat -> stat.setString(1, code),
                beanClass,
                dbAccess
        );
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            Class<? extends DbBeanInterface> beanClass,
            DbBeanParameters parameters,
            String code,
            DBTransaction transaction)
    {
        return getBean(beanClass, parameters, "code", code, transaction);
    }

    public static <B extends DbBeanInterface> Optional<B> getBean(
            Class<? extends DbBeanInterface> beanClass,
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBTransaction transaction)
    {
        return SingleElements.getBean(
                getQuery(parameters, codeField),
                stat -> stat.setString(1, code),
                beanClass,
                transaction
        );
    }

    private static String getQuery(DbBeanParameters parameters, String codeField) {
        return "SELECT id FROM " + parameters.getDatabaseTableName() + " WHERE " + codeField + "=?";
    }

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            E returnedEditor,
            DbBeanParameters parameters,
            String code,
            DBAccess dbAccess)
    {
        return getEditor(returnedEditor, parameters, "code", code, dbAccess);
    }

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            E returnedEditor,
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBAccess dbAccess)
    {
        return SingleElements.getEditor(
                getQuery(parameters, codeField),
                stat -> stat.setString(1, code),
                returnedEditor,
                dbAccess
        );
    }

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            E returnedEditor,
            DbBeanParameters parameters,
            String code,
            DBTransaction transaction)
    {
        return getEditor(returnedEditor, parameters, "code", code, transaction);
    }

    public static <E extends DbBeanEditor> Optional<E> getEditor(
            E returnedEditor,
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBTransaction transaction)
    {
        return SingleElements.getEditor(
                getQuery(parameters, codeField),
                stat -> stat.setString(1, code),
                returnedEditor,
                transaction
        );
    }

    public static long getId(
            DbBeanParameters parameters,
            String code,
            DBAccess dbAccess)
    {
        return getId(parameters, "code", code, dbAccess);
    }

    public static long getId(
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBAccess dbAccess)
    {
        return SingleElements.getID(
                getQuery(parameters, codeField),
                stat -> stat.setString(1, code),
                dbAccess
        );
    }

    public static long getId(
            DbBeanParameters parameters,
            String code,
            DBTransaction transaction)
    {
        return getId(parameters, "code", code, transaction);
    }

    public static long getId(
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBTransaction transaction)
    {
        return SingleElements.getID(
                getQuery(parameters, codeField),
                stat -> stat.setString(1, code),
                transaction
        );
    }

    public static boolean isPresent(
            DbBeanParameters parameters,
            String code,
            DBAccess dbAccess)
    {
        return isPresent(parameters, "code", code, dbAccess);
    }

    public static boolean isPresent(
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBAccess dbAccess)
    {
        return getId(parameters, codeField, code, dbAccess) > 0;
    }

    public static boolean isPresent(
            DbBeanParameters parameters,
            String code,
            DBTransaction transaction)
    {
        return isPresent(parameters, "code", code, transaction);
    }

    public static boolean isPresent(
            DbBeanParameters parameters,
            String codeField,
            String code,
            DBTransaction transaction)
    {
        return getId(parameters, codeField, code, transaction) > 0;
    }

}
