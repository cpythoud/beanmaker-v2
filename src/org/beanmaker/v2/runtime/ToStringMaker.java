package org.beanmaker.v2.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

public class ToStringMaker {

    private final String beanClass;
    private final long idBean;

    private final Map<String, String> fields = new LinkedHashMap<String, String>();

    public ToStringMaker(DbBeanInterface bean) {
        beanClass = bean.getClass().getName();
        idBean = bean.getId();
    }

    public void addField(String name, String value) {
        if (value == null)
            fields.put(name, "null");
        else
            fields.put(name, value);
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
        buf.append("[").append(beanClass).append(" #").append(idBean);

        for (Map.Entry mapEntry: fields.entrySet())
            buf.append(", ").append(mapEntry.getKey()).append("=").append(mapEntry.getValue());

        buf.append("]");
        return buf.toString();
    }
}
