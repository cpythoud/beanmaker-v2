package org.beanmaker.v2.codegen.java;

public class ArrayInitialization extends Expression<ArrayInitialization> {

    private final String type;
    private final int size;

    public ArrayInitialization(String type, int size) {
        this(type, size,  0);
    }

    public ArrayInitialization(String type, int size, int indentationLevel) {
        super("ArrayInit", indentationLevel);
        this.type = type;
        this.size = size;
    }

    @Override
    protected ArrayInitialization getThis() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        startExpression(buf);

        buf.append("new ");
        buf.append(type);
        buf.append("[").append(size).append("]");
        endExpression(buf);

        return buf.toString();
    }

}
