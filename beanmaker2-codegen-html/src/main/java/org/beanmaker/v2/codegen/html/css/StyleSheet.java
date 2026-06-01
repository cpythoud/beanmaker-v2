package org.beanmaker.v2.codegen.html.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StyleSheet {

    private final String name;

    private final Map<String, Style> styles = new HashMap<>();

    public StyleSheet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StyleSheet addStyle(Style style) {
        styles.put(style.getSpecification(), style);

        return this;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int tabs) {
        if (tabs < 0)
            throw new IllegalArgumentException("tabs < 0");

        StringBuilder buf = new StringBuilder();

        var specifications = new ArrayList<>(styles.keySet());
        Collections.sort(specifications);

        for (String specification: specifications)
            buf.append(styles.get(specification).toString(tabs + 1)).append("\n");

        return buf.toString();
    }
}
