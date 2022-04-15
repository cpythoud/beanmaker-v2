package org.beanmaker.v2.runtime.util;

import org.beanmaker.v2.runtime.DbBeanInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

public abstract class DbBeanInventory<B extends DbBeanInterface> {

    protected final Set<B> beans = new HashSet<>();

    public void add(B bean) {
        beans.add(bean);
    }

    public void addAll(Collection<B> beans) {
        this.beans.addAll(beans);
    }

    public List<B> getList() {
        return new ArrayList<>(beans);
    }

    public int getCount() {
        return beans.size();
    }

    protected <K extends Comparable<K>> List<B> getOrderedList(Function<B, K> ordering) {
        var map = new TreeMap<K, B>();
        for (var bean: beans)
            map.put(ordering.apply(bean), bean);
        return new ArrayList<>(map.values());
    }

}
