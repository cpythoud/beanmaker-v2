package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DbBeanLocalization {
    private static final String REQUIRED_EXT = "_required";
    public static final String BAD_FORMAT_EXT = "_bad_format";
    private static final String NOT_UNIQUE_EXT = "_not_unique";

    private final DbBeanLabelBasicFunctions dbBeanLabelBasicFunctions;
    private final String labelNamePrefix;
    private final String beanClassName;
    private DbBeanLanguage language;

    private final List<ErrorMessage> errorMessages = new ArrayList<>();

    public DbBeanLocalization(DbBeanLabelBasicFunctions dbBeanLabelBasicFunctions, String labelNamePrefix, String beanClassName) {
        this(dbBeanLabelBasicFunctions, labelNamePrefix, beanClassName, dbBeanLabelBasicFunctions.getDefaultLanguage());
    }

    public DbBeanLocalization(DbBeanLabelBasicFunctions dbBeanLabelBasicFunctions, String labelNamePrefix, String beanClassName, DbBeanLanguage language) {
        this.dbBeanLabelBasicFunctions = dbBeanLabelBasicFunctions;
        this.labelNamePrefix = labelNamePrefix;
        this.beanClassName = beanClassName;
        this.language = language;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String getBeanVarName() {
        return Strings.uncapitalize(beanClassName);
    }

    public void setLanguage(DbBeanLanguage language) {
        this.language = language;
    }

    public DbBeanLanguage getLanguage() {
        return language;
    }

    public String getLabel(String coreLabelName) {
        return dbBeanLabelBasicFunctions.get(labelNamePrefix, coreLabelName, language);
    }

    public String getLabel(String coreLabelName, List<Object> parameters) {
        return dbBeanLabelBasicFunctions.get(labelNamePrefix, coreLabelName, language, parameters);
    }

    public String getRequiredErrorMessage(String field) {
        return getLabel(field + REQUIRED_EXT);
    }

    public String getBadFormatErrorMessage(String labelName, List<Object> parameters) {
        if (parameters == null || parameters.isEmpty())
            return getLabel(labelName);

        return getLabel(labelName, parameters);
    }

    public String getNotUniqueErrorMessage(String field) {
        return getLabel(field + NOT_UNIQUE_EXT);
    }


    public void clearErrorMessages() {
        errorMessages.clear();
    }

    public void addErrorMessage(long beanId, String fieldName, String fieldLabel, String message) {
        errorMessages.add(new ErrorMessage(beanId, fieldName, fieldLabel, message));
    }

    public List<ErrorMessage> getErrorMessages() {
        return Collections.unmodifiableList(errorMessages);
    }


    public Locale getLocale() {
        if (language == null)
            throw new IllegalStateException("No language specified (language = null), cannot retrieve Locale");

        return language.getLocale();
    }
}
