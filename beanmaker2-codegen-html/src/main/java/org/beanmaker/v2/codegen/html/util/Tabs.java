package org.beanmaker.v2.codegen.html.util;

public class Tabs {

    public static String getTabs(final int count) {
        if (count < 0)
            throw new IllegalArgumentException("count < 0");

        if (count == 0)
            return "";

        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < count; ++i)
            buf.append("\t");

        return buf.toString();
    }
}
