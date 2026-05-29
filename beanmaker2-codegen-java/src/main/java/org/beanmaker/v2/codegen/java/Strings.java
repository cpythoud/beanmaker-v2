package org.beanmaker.v2.codegen.java;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

final class Strings {

    static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    static String concatWithSeparator(String separator, final String... strings) {
        return concatWithSeparator(separator, Arrays.asList(strings));
    }

    static String concatWithSeparator(String separator, List<String> strings) {
        return concatStringCollectionWithSeparator(separator, strings);
    }

    private static String concatStringCollectionWithSeparator(final String separator, final Collection<String> strings) {
        if (strings.isEmpty())
            return "";

        final StringBuilder buf = new StringBuilder();

        for (String s: strings) {
            buf.append(s);
            buf.append(separator);
        }
        buf.delete(buf.length() - separator.length(), buf.length());

        return buf.toString();
    }

    static String quickQuote(final String string) {
        return "\"" + string + "\"";
    }

    private Strings() { }

}
