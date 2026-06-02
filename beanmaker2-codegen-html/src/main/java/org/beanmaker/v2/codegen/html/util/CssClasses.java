package org.beanmaker.v2.codegen.html.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ...
 */
public class CssClasses {

    private final Set<String> classNames = new HashSet<>();

    public static CssClasses start(String firstClasses) {
        CssClasses cssClasses = new CssClasses();
        return cssClasses.add(firstClasses);
    }

    public CssClasses add(String classNames) {
        if (classNames == null || classNames.isEmpty())
            return this;

        this.classNames.addAll(splitClassNames(classNames));

        return this;
    }

    private List<String> splitClassNames(String classNames) {
        return List.of(classNames.split("\\s+"));
    }

    public CssClasses add(String className, boolean condition) {
        if (condition)
            return add(className);

        return this;
    }

    public String get() {
        if (classNames.isEmpty())
            return "";

        var classList = new StringBuilder();
        for (String className: classNames)
            classList.append(className).append(" ");
        classList.delete(classList.length() -1, classList.length());

        return classList.toString();
    }

    public void reset() {
        classNames.clear();
    }

    public int count() {
        return classNames.size();
    }

    public boolean isEmpty() {
        return classNames.isEmpty();
    }

}
