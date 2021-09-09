package org.beanmaker.v2.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class BeanInternals {
    private static final String REQUIRED_EXT = "_required";
    private static final String BAD_FORMAT_EXT = "_bad_format";
    private static final String NOT_UNIQUE_EXT = "_not_unique";

    private final String resourceBundleName;
    private ResourceBundle resourceBundle;
    private Locale locale;

    private List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();


    public BeanInternals(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
        setLocale(Locale.getDefault());
    }


    public void setLocale(Locale locale) {
        this.locale = locale;
        resourceBundle = ResourceBundle.getBundle(resourceBundleName, locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLabel(String field) {
        return resourceBundle.getString(field);
    }

    public String getRequiredErrorMessage(String field) {
        return getLabel(field + REQUIRED_EXT);
    }

    public String getBadFormatErrorMessage(String field) {
        return getLabel(field + BAD_FORMAT_EXT);
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
}
