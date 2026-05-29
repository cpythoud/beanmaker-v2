package org.beanmaker.v2.codegen.java;

public class Decrement extends IncrementDecrement<Decrement> {

    public Decrement(String variable) {
        this(variable, IncDecPosition.BEFORE);
    }

    public Decrement(String variable, IncDecPosition position) {
        this(variable, position, 0);
    }

    public Decrement(String variable, int indentationLevel) {
        this(variable, IncDecPosition.BEFORE, indentationLevel);
    }

    public Decrement(String variable, IncDecPosition position, int indentationLevel) {
        super("--", variable, position, indentationLevel);
    }


    @Override
    protected Decrement getThis() {
        return this;
    }
}
