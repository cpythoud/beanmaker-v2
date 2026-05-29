package org.beanmaker.v2.codegen.java;

public class Increment extends IncrementDecrement<Increment> {

    public Increment(String variable) {
        this(variable, IncDecPosition.BEFORE);
    }

    public Increment(String variable, IncDecPosition position) {
        this(variable, position, 0);
    }

    public Increment(String variable, int indentationLevel) {
        this(variable, IncDecPosition.BEFORE, indentationLevel);
    }

    public Increment(String variable, IncDecPosition position, int indentationLevel) {
        super("++", variable, position, indentationLevel);
    }


    @Override
    protected Increment getThis() {
        return this;
    }
}
