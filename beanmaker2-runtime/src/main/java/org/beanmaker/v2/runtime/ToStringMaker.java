package org.beanmaker.v2.runtime;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ToStringMaker {

    private final String javaClass;
    private final long databaseID;

    private final Map<String, String> fields = new LinkedHashMap<String, String>();

    public ToStringMaker(DbBeanInterface bean) {
        javaClass = bean.getClass().getName();
        databaseID = bean.getId();
    }

    public ToStringMaker(DbBeanEditor editor) {
        javaClass = editor.getClass().getName();
        databaseID = editor.getId();
    }

    public ToStringMaker(String javaClassName, long beanID) {
        javaClass = javaClassName;
        databaseID = beanID;
    }

    public void addField(String name, String value) {
        fields.put(name, Objects.requireNonNullElse(value, "null"));
    }

    public void addField(String name, Object value) {
        if (value == null)
            fields.put(name, "null");
        else
            addField(name, value.toString());
    }

    public void addField(String name, boolean value) {
        addField(name, Boolean.toString(value));
    }

    public void addField(String name, int value) {
        addField(name, Integer.toString(value));
    }

    public void addField(String name, long value) {
        addField(name, Long.toString(value));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(javaClass).append(" #").append(databaseID);

        for (Map.Entry mapEntry: fields.entrySet())
            buf.append(", ").append(mapEntry.getKey()).append("=").append(mapEntry.getValue());

        buf.append("]");
        return buf.toString();
    }

}
