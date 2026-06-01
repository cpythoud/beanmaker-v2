package org.beanmaker.v2.codegen.html.util;

import org.beanmaker.v2.codegen.html.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class facilitates creating tags with composite properties.
 * <p>
 * For example: &lt;li data="theme: test, source: server"&gt;
 */
public class CompositeProperty {
    private final String name;
    private final Map<String, List<String>> entries = new HashMap<String, List<String>>();

    public CompositeProperty(final String name) {
        this.name = name;
    }

    public void addEntry(final String key, final String value) {
        List<String> values = entries.get(key);
        if (values == null) {
            values = new ArrayList<String>();
            values.add(value);
            entries.put(key,  values);
        } else
            values.add(value);
    }

    public void addPropertyTo(final Tag element) {
        element.attribute(name, getValues());
    }

    private String getValues() {
        final StringBuilder buf = new StringBuilder();

        for (String key: entries.keySet())
            for (String val: entries.get(key))
                buf.append(key).append(": ").append(val).append(", ");

        buf.delete(buf.length() - 2, buf.length());

        return buf.toString();
    }
}
