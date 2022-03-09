package org.beanmaker.v2.runtime;

import java.util.Locale;

public interface DbBeanLanguage extends DbBeanInterface {

    String getName();

    String getIso();
    String getCapIso();

    Locale getLocale();
}
