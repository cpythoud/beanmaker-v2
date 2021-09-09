package org.beanmaker.v2.runtime;

import java.util.Comparator;

public class MultilingualComparator<T extends MultilingualIdNamePairBean> implements Comparator<T> {

    private final DbBeanLanguage dbBeanLanguage;

    public MultilingualComparator(DbBeanLanguage dbBeanLanguage) {
        this.dbBeanLanguage = dbBeanLanguage;
    }

    @Override
    public int compare(T bean1, T bean2) {
        return bean1.getNameForPair(dbBeanLanguage).compareTo(bean2.getNameForPair(dbBeanLanguage));
    }
}
