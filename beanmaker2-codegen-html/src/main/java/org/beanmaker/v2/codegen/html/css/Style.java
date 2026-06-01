package org.beanmaker.v2.codegen.html.css;

import org.beanmaker.v2.codegen.html.util.Tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Style {

    private final String specification;

    private final Map<String, String> rules = new HashMap<String, String>();

    public Style(final String specification) {
        this.specification = specification;
    }

    public String getSpecification() {
        return specification;
    }

    public Style addRule(final String element, final String value) {
        rules.put(element, value);

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

        buf.append(Tabs.getTabs(tabs))
                .append(specification)
                .append("{\n");

        final List<String> elements = new ArrayList<String>();
        elements.addAll(rules.keySet());
        Collections.sort(elements);

        for (String element: elements)
            buf.append(Tabs.getTabs(tabs + 1))
                    .append(element)
                    .append(": ")
                    .append(rules.get(element))
                    .append(";\n");

        buf.append(Tabs.getTabs(tabs))
                .append("}\n");

        return buf.toString();
    }
}
