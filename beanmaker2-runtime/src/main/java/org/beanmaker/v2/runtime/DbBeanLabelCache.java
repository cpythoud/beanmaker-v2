package org.beanmaker.v2.runtime;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum DbBeanLabelCache {
    INSTANCE;

    private final ConcurrentMap<Long, DbBeanLabel> fromID = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, DbBeanLabel> fromName = new ConcurrentHashMap<>();

    public static void cache(DbBeanLabel label) {
        INSTANCE.fromID.putIfAbsent(label.getId(), label);
        INSTANCE.fromName.putIfAbsent(label.getName(), label);
    }

    public static DbBeanLabel get(long id) {
        return INSTANCE.fromID.get(id);
    }

    public static DbBeanLabel get(String name) {
        return INSTANCE.fromName.get(name);
    }

    public static DbBeanLabel getAndRefresh(long id) {
        var label = INSTANCE.fromID.get(id);
        label.cacheLabelsFromDB();
        return label;
    }

    public static DbBeanLabel getAndRefresh(String name) {
        var label = INSTANCE.fromName.get(name);
        label.cacheLabelsFromDB();
        return label;
    }

    public static void remove(DbBeanLabel label) {
        INSTANCE.fromID.remove(label.getId());
        INSTANCE.fromName.remove(label.getName());
    }

    public static void remove(long id) {
        var label = INSTANCE.fromID.get(id);
        if (label != null)
            remove(label);
    }

    public static void remove(String name) {
        var label = INSTANCE.fromName.get(name);
        if (label != null)
            remove(label);
    }

    public static void clear() {
        INSTANCE.fromID.clear();
        INSTANCE.fromName.clear();
    }

}
