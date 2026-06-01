package org.beanmaker.v2.codegen.html.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyleSheet {

    private final String name;

    private final Map<String, Style> styles = new HashMap<String, Style>();

    public StyleSheet(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StyleSheet addStyle(final Style style) {
        styles.put(style.getSpecification(), style);

        return this;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(final int tabs) {
        if (tabs < 0)
            throw new IllegalArgumentException("tabs < 0");

        final StringBuilder buf = new StringBuilder();

        final List<String> specifications = new ArrayList<String>();
        specifications.addAll(styles.keySet());
        Collections.sort(specifications);

        for (String specification: specifications)
            buf.append(styles.get(specification).toString(tabs + 1)).append("\n");

        return buf.toString();
    }
}
