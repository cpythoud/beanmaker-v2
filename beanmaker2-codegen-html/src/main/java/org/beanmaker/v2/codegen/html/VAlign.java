package org.beanmaker.v2.codegen.html;

/**
 * ...
 */
public enum  VAlign {
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom"),
    BASELINE("baseline");

    private final String val;

    VAlign(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

}
