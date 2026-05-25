package org.beanmaker.v2.util;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public final class AlphabeticalSorter {

    private AlphabeticalSorter() { }

    public static <T> List<T> sort(
            Collection<T> values,
            Locale locale,
            Function<T, String> textExtractor)
    {
        var collator = Collator.getInstance(locale != null ? locale : Locale.getDefault());
        collator.setStrength(Collator.PRIMARY);

        var sortedValues = new ArrayList<>(values);
        sortedValues.sort(Comparator.comparing(
                textExtractor,
                Comparator.nullsFirst(collator)
        ));
        return sortedValues;
    }

}
