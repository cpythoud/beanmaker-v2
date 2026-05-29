package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class StringOrCode<T> {

    private final String string;
    private final T code;

    public StringOrCode(String string) {
        this.string = string;
        code = null;
    }

    public StringOrCode(T code) {
        this.code = code;
        string = null;
    }

    public static <T> List<String> getStrings(List<StringOrCode<T>> alternatives) {
        List<String> strings = new ArrayList<>();
        for (StringOrCode<T> alternative: alternatives)
            strings.add(alternative.toString());
        return strings;
    }

    public boolean isString() {
        return code == null;
    }

    public boolean isCode() {
        return string == null;
    }

    public String getString() {
        return string;
    }

    public T getCode() {
        return code;
    }

    @Override
    public String toString() {
        if (string == null)
            return code.toString();
        return string;
    }

}
