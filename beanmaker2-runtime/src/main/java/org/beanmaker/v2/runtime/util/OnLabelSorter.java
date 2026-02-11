package org.beanmaker.v2.runtime.util;

import org.beanmaker.v2.runtime.DbBeanInterface;
import org.beanmaker.v2.runtime.DbBeanLanguage;
import org.beanmaker.v2.runtime.DbBeanLocalization;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class OnLabelSorter<B extends DbBeanInterface> {

    private final List<Item> items = new ArrayList<>();

    private class Item {
        private final String label;
        private final B bean;

        private Item(String label, B bean) {
            this.label = label;
            this.bean = bean;
        }
    }

    public static <B extends DbBeanInterface> List<B> sort(List<B> beans, DbBeanLocalization localization) {
        return sort(beans, localization.getLanguage());
    }

    public static <B extends DbBeanInterface> List<B> sort(List<B> beans, DbBeanLanguage language) {
        var sorter = new OnLabelSorter<B>();
        for (var bean: beans)
            sorter.add(bean.getNameForIdNamePairsAndTitles(language), bean);
        return sorter.getSortedBeans(language);
    }

    public void clear() {
        items.clear();
    }

    public void add(String label, B bean) {
        items.add(new Item(label, bean));
    }

    public List<B> getSortedBeans(DbBeanLocalization localization) {
        return getSortedBeans(localization.getLanguage());
    }

    public List<B> getSortedBeans(DbBeanLanguage language) {
        return getSortedBeans(language.getLocale());
    }

    public List<B> getSortedBeans(Locale locale) {
        var collator = Collator.getInstance(locale != null ? locale : Locale.getDefault());

        var sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparing(
                item -> item.label,
                Comparator.nullsFirst(collator)
        ));

        var beans = new ArrayList<B>(sortedItems.size());
        for (var item : sortedItems) {
            beans.add(item.bean);
        }
        return beans;
    }

}
