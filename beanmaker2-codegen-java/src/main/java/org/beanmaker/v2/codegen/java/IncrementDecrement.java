package org.beanmaker.v2.codegen.java;

abstract class IncrementDecrement<T extends IncrementDecrement<T>> extends Expression<T>  {

    private final String operation;
    private final String variable;
    private final IncDecPosition position;

    IncrementDecrement(String operation, String variable, IncDecPosition position, int indentationLevel) {
        super(variable, indentationLevel);
        this.operation = operation;
        this.variable = variable;
        this.position = position;
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        startExpression(buf);

        if (position.equals(IncDecPosition.BEFORE))
            buf.append(operation);
        buf.append(variable);
        if (position.equals(IncDecPosition.AFTER))
            buf.append(operation);

        endExpression(buf);

        return buf.toString();
    }
}
