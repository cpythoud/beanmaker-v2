package org.beanmaker.v2.runtime;

import java.util.Locale;
import java.util.ResourceBundle;

public class BaseView {
    final private String resourceBundleName;
    protected ResourceBundle resourceBundle;
    protected Locale locale;

    public BaseView(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
        initLocale(Locale.getDefault());
    }

    private void initLocale(Locale locale) {
        this.locale = locale;
        resourceBundle = ResourceBundle.getBundle(resourceBundleName, locale);
    }

    public void setLocale(final Locale locale) {
        initLocale(locale);
    }
}
