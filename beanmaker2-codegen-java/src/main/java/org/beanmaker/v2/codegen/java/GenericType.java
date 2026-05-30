package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class GenericType {

    private final String stringRepresentation;

    public GenericType(String type, String... args) {
        stringRepresentation = type +
                "<" +
                Strings.concatWithSeparator(", ", args) +
                ">";
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

}
