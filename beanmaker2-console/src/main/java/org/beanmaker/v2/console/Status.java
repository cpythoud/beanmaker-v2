package org.beanmaker.v2.console;

public enum Status {
    OK("green", null),
    NOTICE("blue", "Notice:"),
    WARNING("yellow", "Warning:"),
    ERROR("red", "Error:");

    private final String color;
    private final String prefix;

    Status(String color, String prefix) {
        this.color = color;
        this.prefix = prefix;
    }

    String getColor() {
        return color;
    }

    String getPrefix() {
        return prefix;
    }

}
