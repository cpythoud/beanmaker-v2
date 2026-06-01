package org.beanmaker.v2.codegen.html.util;

public final class Tabs {

    public static String getTabs(int count) {
        if (count < 0)
            throw new IllegalArgumentException("count < 0");

        if (count == 0)
            return "";

        var buf = new StringBuilder();
        buf.repeat("\t", count);
        return buf.toString();
    }

}
