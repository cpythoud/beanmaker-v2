package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ObjectCreation extends Expression<ObjectCreation> {

    private final String object;

    public ObjectCreation(String object) {
        this(object,  0);
    }

    public ObjectCreation(String object, int indentationLevel) {
        super("Object", indentationLevel);
        this.object = object;
    }

    @Override
    protected ObjectCreation getThis() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        startExpression(buf);

        buf.append("new ");
        buf.append(object);
        appendArguments(buf);

        endExpression(buf);

        return buf.toString();
    }

}
