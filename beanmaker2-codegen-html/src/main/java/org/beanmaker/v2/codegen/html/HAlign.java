package org.beanmaker.v2.codegen.html;

/**
 * ...
 */
public enum HAlign {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right"),
    JUSTIFY("justify"),
    CHAR("char");

    private final String val;

    HAlign(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
