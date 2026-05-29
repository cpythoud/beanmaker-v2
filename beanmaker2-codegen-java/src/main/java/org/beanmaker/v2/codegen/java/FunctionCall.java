package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class FunctionCall extends Expression<FunctionCall> {

    private final String function;
    private final StringOrCode<Expression> object;


    public FunctionCall(final String function) {
        this(function, 0);
    }

    public FunctionCall(final String function, final int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = null;
    }

    public FunctionCall(final String function, final String object) {
        this(function, object, 0);
    }

    public FunctionCall(final String function, final String object, final int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = new StringOrCode<>(object);
    }

    public FunctionCall(final String function, final Expression object) {
        this(function, object, 0);
    }

    public FunctionCall(final String function, final Expression object, final int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = new StringOrCode<>(object);
    }


    @Override
    protected FunctionCall getThis() {
        return this;
    }

    public boolean isObjectMethodCall() {
        return object != null;
    }

    public String getFunctionName() {
        return function;
    }


    @Override
    public String toString() {
        var buf = new StringBuilder();

        startExpression(buf);

        if (object != null)
            buf.append(object).append(".");

        addCallAndArguments(buf);

        endExpression(buf);

        return buf.toString();
    }

    void addCallAndArguments(StringBuilder buf) {
        buf.append(function);
        appendArguments(buf);
    }

}
