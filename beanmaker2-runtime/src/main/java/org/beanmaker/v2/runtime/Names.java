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
        var names = new ArrayList<String>();

        for (B bean: beans)
            names.add(bean.getDisplayName());

        return names;
    }

    public static <B extends NamedDbBean> List<String> getSortedList(Collection<B> beans) {
        var names = getList(beans);
        Collections.sort(names);
        return names;
    }

    public static <B extends NamedDbBean> void sortList(List<B> beans) {
        beans.sort(Comparator.comparing(NamedDbBean::getDisplayName));
    }

    public static <B extends NamedDbBean> void sortList(List<B> beans, Locale locale) {
        var collator = Collator.getInstance(locale);
        collator.setStrength(Collator.PRIMARY);
        beans.sort((bean1, bean2) -> collator.compare(bean1.getDisplayName(), bean2.getDisplayName()));
    }

    public static <B extends MultiLingualNamedDbBean> List<String> getList(
            Collection<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        var names = new ArrayList<String>();

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
        var collator = Collator.getInstance(dbBeanLanguage.getLocale());
        collator.setStrength(Collator.PRIMARY);
        beans.sort((bean1, bean2) ->
                collator.compare(bean1.getDisplayName(dbBeanLanguage), bean2.getDisplayName(dbBeanLanguage)));
    }

    public static <B extends DbBeanMultilingual> List<String> getListFromMultilingualBeans(
            Collection<B> beans,
            DbBeanLanguage dbBeanLanguage)
    {
        var names = new ArrayList<String>();

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
