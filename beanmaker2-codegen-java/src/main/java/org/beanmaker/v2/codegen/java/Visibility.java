package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public enum Visibility {
    PUBLIC("public"),
    PROTECTED("protected"),
    PACKAGE_PRIVATE(""),
    PRIVATE("private"),
    NONE("");

    private final String val;

    Visibility(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

}
