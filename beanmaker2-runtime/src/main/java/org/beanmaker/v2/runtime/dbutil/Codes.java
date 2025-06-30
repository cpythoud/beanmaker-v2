package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.runtime.DbBeanEditorWithUniqueCode;
import org.beanmaker.v2.runtime.DbBeanParameters;
import org.beanmaker.v2.runtime.DbBeanWithUniqueCode;

import org.beanmaker.v2.util.Strings;

import org.dbbeans.sql.DBAccess;
import org.dbbeans.sql.DBTransaction;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class Codes {

    public static final String STANDARD_CODE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789_-";

    public static final Pattern SHORT_VALIDATION_PATTERN = Pattern.compile("[a-z0-9_-]{4,20}");
    public static final Pattern LONG_VALIDATION_PATTERN = Pattern.compile("[a-z0-9_-]{4,64}");

    private Codes() { }

    public static boolean validateFormat(String code, String regex) {
        return Pattern.compile(regex).matcher(code).matches();
    }

    public static boolean validateFormat(String code, Pattern pattern) {
        return pattern.matcher(code).matches();
    }

    public static <B extends DbBeanWithUniqueCode> Optional<B> getBean(
            Class<? extends DbBeanWithUniqueCode> beanClass,
            DbBeanParameters parameters,
            String code,
            DBAccess dbAccess)
    {
        return getBean(beanClass, parameters, "code", code, dbAccess);
    }

    public static <B extends DbBeanWithUniqueCode> Optional<B> getBean(
            Class<? extends DbBeanWithUniqueCode> beanClass,
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

    public static <B extends DbBeanWithUniqueCode> Optional<B> getBean(
            Class<? extends DbBeanWithUniqueCode> beanClass,
            DbBeanParameters parameters,
            String code,
            DBTransaction transaction)
    {
        return getBean(beanClass, parameters, "code", code, transaction);
    }

    public static <B extends DbBeanWithUniqueCode> Optional<B> getBean(
            Class<? extends DbBeanWithUniqueCode> beanClass,
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

    public static <E extends DbBeanEditorWithUniqueCode> Optional<E> getEditor(
            E returnedEditor,
            DbBeanParameters parameters,
            String code,
            DBAccess dbAccess)
    {
        return getEditor(returnedEditor, parameters, "code", code, dbAccess);
    }

    public static <E extends DbBeanEditorWithUniqueCode> Optional<E> getEditor(
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

    public static <E extends DbBeanEditorWithUniqueCode> Optional<E> getEditor(
            E returnedEditor,
            DbBeanParameters parameters,
            String code,
            DBTransaction transaction)
    {
        return getEditor(returnedEditor, parameters, "code", code, transaction);
    }

    public static <E extends DbBeanEditorWithUniqueCode> Optional<E> getEditor(
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

    public static void adjustCopyCode(DbBeanEditorWithUniqueCode copy) {
        copy.setCode(copy.getCode() + "_copy");
        if (!copy.isCodeUnique()) {
            String baseCode = copy.getCode();
            int index = 2;
            do {
                copy.setCode(baseCode + index);
                ++index;
            } while (!copy.isCodeUnique());
        }
    }

    public static String createStandardizedCode(String source) {
        return Strings.replaceUnknownChars(Strings.removeAccents(source).toLowerCase(), STANDARD_CODE_CHARACTERS);
    }

    public static void standardizeCodes(List<DbBeanEditorWithUniqueCode> beans, DBTransaction dbTransaction) {
        Transactions.wrap(
                transaction -> {
                    for (var bean : beans) {
                        bean.setCode(createStandardizedCode(bean.getCode()));
                        bean.updateDB(transaction);
                    }
                },
                dbTransaction
        );
    }

}
