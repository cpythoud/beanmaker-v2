package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public abstract class Expression<T extends Expression<T>> extends CallWithArgs<T> {

    private boolean embedded = true;

    public Expression(String keyword, int indentationLevel) {
        super(keyword, indentationLevel);
    }

    public T byItself() {
        embedded = false;
        return getThis();
    }


    protected void startExpression(StringBuilder buf) {
        if (!embedded)
            buf.append(getTabs());
    }

    protected void endExpression(StringBuilder buf) {
        if (!embedded)
            buf.append(";\n");
    }

}
