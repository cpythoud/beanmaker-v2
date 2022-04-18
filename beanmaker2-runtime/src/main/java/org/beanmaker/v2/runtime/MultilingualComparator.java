package org.beanmaker.v2.runtime;

import java.text.Collator;

import java.util.Comparator;

public class MultilingualComparator<T extends DbBeanInterface> implements Comparator<T> {

    private final DbBeanLanguage dbBeanLanguage;
    private final Collator collator;

    public MultilingualComparator(DbBeanLanguage dbBeanLanguage) {
        this.dbBeanLanguage = dbBeanLanguage;
        collator = Collator.getInstance(dbBeanLanguage.getLocale());
    }

    @Override
    public int compare(T bean1, T bean2) {
        return collator.compare(
                bean1.getNameForIdNamePairsAndTitles(dbBeanLanguage),
                bean2.getNameForIdNamePairsAndTitles(dbBeanLanguage));
    }

}
