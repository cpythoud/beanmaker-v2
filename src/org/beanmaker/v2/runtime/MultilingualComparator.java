package org.beanmaker.v2.runtime;

import java.util.Comparator;

public class MultilingualComparator<T extends DbBeanInterface> implements Comparator<T> {

    private final DbBeanLanguage dbBeanLanguage;

    public MultilingualComparator(DbBeanLanguage dbBeanLanguage) {
        this.dbBeanLanguage = dbBeanLanguage;
    }

    @Override
    public int compare(T bean1, T bean2) {
        return bean1.getNameForIdNamePairsAndTitles(dbBeanLanguage).compareTo(bean2.getNameForIdNamePairsAndTitles(dbBeanLanguage));
    }
}
