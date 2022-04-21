package org.beanmaker.v2.runtime.util;

import org.beanmaker.v2.runtime.DbBeanLanguage;
import org.beanmaker.v2.runtime.DbBeanLocalization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Languages {

    private static final DateTimeFormatter MONTH_NAMES = DateTimeFormatter.ofPattern("MMMM");

    public static String getMonthName(DbBeanLanguage language, int month) {
        return MONTH_NAMES.withLocale(language.getLocale()).format(LocalDate.of(2000, month, 1));
    }

    public static String getMonthName(DbBeanLocalization localization, int month) {
        return getMonthName(localization.getLanguage(), month);
    }

}
