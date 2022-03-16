package org.beanmaker.v2.runtime;

import java.util.Locale;

public interface DbBeanLanguage extends DbBeanInterface {

    String getName();

    String getIso();

    default String getCapIso() {
        return getIso().toUpperCase();
    }

    default Locale getLocale() {
        return new Locale(getIso());
    }

}
