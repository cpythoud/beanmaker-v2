package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class GenericType {

    private final String stringRepresentation;

    public GenericType(final String type, final String... args) {
        final StringBuilder buf = new StringBuilder();

        buf.append(type);
        buf.append("<");
        buf.append(Strings.concatWithSeparator(", ", args));
        buf.append(">");

        stringRepresentation = buf.toString();
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }
}
