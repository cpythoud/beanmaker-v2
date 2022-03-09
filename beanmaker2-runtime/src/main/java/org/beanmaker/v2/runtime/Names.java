package org.beanmaker.v2.runtime;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Names {

    public static <B extends NamedDbBean> List<String> getList(Collection<B> beans) {
        List<String> names = new ArrayList<String>();

        for (B bean: beans)
            names.add(bean.getDisplayName());

        return names;
    }

    public static <B extends NamedDbBean> List<String> getSortedList(Collection<B> beans) {
        List<String> names = getList(beans);
        Collections.sort(names);
        return names;
    }

    public static <B extends NamedDbBean> void sortList(List<B> beans) {
        Collections.sort(beans, new Comparator<B>() {
            @Override
            public int compare(B bean1, B bean2) {
                return bean1.getDisplayName().compareTo(bean2.getDisplayName());
            }
        });
    }

    public static <B extends NamedDbBean> void sortList(List<B> beans, Locale locale) {
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.PRIMARY);
        Collections.sort(beans, new Comparator<B>() {
            @Override
            public int compare(B bean1, B bean2) {
                return collator.compare(bean1.getDisplayName(), bean2.getDisplayName());
            }
        });
    }

    public static <B extends MultiLingualNamedDbBean> List<String> getList(
            Collection<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        List<String> names = new ArrayList<String>();

        for (B bean: beans)
            names.add(bean.getDisplayName(dbBeanLanguage));

        return names;
    }

    public static <B extends MultiLingualNamedDbBean> List<String> getSortedList(
            Collection<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        List<String> names = getList(beans, dbBeanLanguage);
        Collections.sort(names);
        return names;
    }

    public static <B extends MultiLingualNamedDbBean> void sortList(
            List<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        Collator collator = Collator.getInstance(dbBeanLanguage.getLocale());
        collator.setStrength(Collator.PRIMARY);
        Collections.sort(beans, new Comparator<B>() {
            @Override
            public int compare(B bean1, B bean2) {
                return collator.compare(bean1.getDisplayName(dbBeanLanguage), bean2.getDisplayName(dbBeanLanguage));
            }
        });
    }

    public static <B extends DbBeanMultilingual> List<String> getListFromMultilingualBeans(
            Collection<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        List<String> names = new ArrayList<String>();

        for (B bean: beans)
            names.add(bean.getLabel(dbBeanLanguage));

        return names;
    }

    public static <B extends DbBeanMultilingual> List<String> getSortedListFromMultilingualBeans(
            Collection<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        List<String> names = getListFromMultilingualBeans(beans, dbBeanLanguage);
        Collections.sort(names);
        return names;
    }

}
