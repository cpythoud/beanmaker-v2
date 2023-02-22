package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Ids {

    public static String getProtectionCodeFromParameterName(String parameterName) {
        return getProtectionCodeFromParameterName(parameterName, "_");
    }

    public static String getProtectionCodeFromParameterName(String parameterName, String separatorRegex) {
        String[] elements = parameterName.split(separatorRegex);
        if (elements.length < 2)
            throw new IllegalArgumentException("Could not separate id/code from name based on regex: "
                    + separatorRegex);

        return elements[elements.length - 1];
    }

    public static long getIdFromParameterName(String parameterName) {
        return Long.parseLong(getProtectionCodeFromParameterName(parameterName));
    }

    public static long getIdFromParameterName(String parameterName, String separatorRegex) {
        return Long.parseLong(getProtectionCodeFromParameterName(parameterName, separatorRegex));
    }

    public static <T extends DbBeanInterface> Set<Long> getIdSet(Collection<T> beans) {
        return getIdSet(beans, new HashSet<>());
    }

    public static <T extends DbBeanInterface> Set<Long> getIdSet(Collection<T> beans, Set<Long> set) {
        for (DbBeanInterface bean: beans)
            set.add(bean.getId());

        return set;
    }

    public static <T extends DbBeanInterface> String getAggregatedIdString(
            Collection<T> beans,
            String separator)
    {
        var ids = new ArrayList<>(getIdSet(beans));

        Collections.sort(ids);

        return Strings.concatWithSeparator(separator, Strings.asListOfStrings(ids));
    }

    public static Set<Long> getIdSet(String aggregatedIds, String separator, boolean lenient) {
        var set = new HashSet<Long>();

        int index = 0;
        for (String val: aggregatedIds.split(separator)) {
            ++index;
            long id = Strings.getLongVal(val);
            if (id == 0) {
                if (!lenient)
                    throw new IllegalArgumentException("Invalid value for id @pos #" + index + ", val = " + val);
            } else {
                set.add(id);
            }
        }

        if (set.isEmpty() && !lenient)
            throw new IllegalArgumentException("No id to extract");

        return set;
    }

}
