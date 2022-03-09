package org.beanmaker.v2.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains simple utility functions to manipulate strings in the context of the English language.
 */
public class English {

    private static final List<String> UNCOUNTABLE =
            Arrays.asList("deer", "equipment", "fish", "information", "money", "rice", "series", "sheep", "species");

    private static final Map<String, String> PLURALIZATION_MAPPING;
    private static final Map<String, String> SINGULARIZATION_MAPPING;

    static {
        Map<String, String> map = new LinkedHashMap<String, String>();

        map.put("child", "children");
        map.put("foot", "feet");
        map.put("goose", "geese");
        map.put("ox", "oxen");
        map.put("person", "people");
        map.put("tooth", "teeth");

        map.put("(.*)fe?", "$1ves");
        map.put("(.*)man$", "$1men");
        map.put("(.+[aeiou]y)$", "$1s");
        map.put("(.+[^aeiou])y$", "$1ies");
        map.put("(.+z)$", "$1zes");
        map.put("([m|l])ouse$", "$1ice");
        map.put("(.+)(e|i)x$", "$1ices");
        map.put("(.+(s|x|sh|ch))$", "$1es");
        map.put("(.+)", "$1s");

        PLURALIZATION_MAPPING = map;
    }

    static {
        Map<String, String> map = new LinkedHashMap<String, String>();

        map.put("children", "child");
        map.put("feet", "foot");
        map.put("geese", "goose");
        map.put("indices", "index");
        map.put("matrices", "matrix");
        map.put("oxen", "ox");
        map.put("people", "person");
        map.put("teeth", "tooth");

        map.put("(.*)ives?", "$1ife");
        map.put("(.*)ves?", "$1f");
        map.put("(.*)men$", "$1man");
        map.put("(.+[aeiou])ys$", "$1y");
        map.put("(.+[^aeiou])ies$", "$1y");
        map.put("(.+)zes$", "$1");
        map.put("([m|l])ice$", "$1ouse");
        map.put("(.+[aeiou][aeiou](s|x|sh|ch|tch))es$", "$1e");
        map.put("(.+[aeiou](s|x|sh|ch|tch))es$", "$1");
        map.put("(.+)s", "$1");

        SINGULARIZATION_MAPPING = map;
    }

    /**
     * Take a String representing the singular of an english noun and returns its plural form.
     * The function expect its input to be lowercase and will not work with all uppercase words. It might work with
     * capitalized words.
     * The code for this function was adapted from C# code created by Matt Grande. More information can be found
     * <a href="https://mattgrande.wordpress.com/2009/10/28/pluralization-helper-for-c/">there</a>.
     * @param singular the lowercase English noun to be pluralized
     * @return plural of singular parameter
     */
    public static String pluralize(String singular) {
        return transform(singular, PLURALIZATION_MAPPING);
    }

    /**
     * Take a String representing the plural of an english noun and returns its singular form.
     * The function expect its input to be lowercase and will not work with all uppercase words. It might work with
     * capitalized words.
     * The code for this function was adapted from C# code created by Mattias Fagerlund. More information can be found
     * <a href="https://lotsacode.wordpress.com/2010/03/05/singularization-pluralization-in-c/">there</a>.
     * @param plural the lowercase plural English noun whose singular we are interested in
     * @return singular of plural parameter
     */
    public static String singularize(String plural) {
        return transform(plural, SINGULARIZATION_MAPPING);
    }

    private static String transform(String word, Map<String, String> conversionMap) {
        if (Strings.isEmpty(word))
            throw new IllegalArgumentException("Cannot convert empty or null String");

        if (UNCOUNTABLE.contains(word))
            return word;

        for (String pattern: conversionMap.keySet())
            if (word.matches(pattern))
                return word.replaceAll(pattern, conversionMap.get(pattern));

        return word;
    }

}
