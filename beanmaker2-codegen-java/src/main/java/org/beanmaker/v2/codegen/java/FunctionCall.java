package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class FunctionCall extends Expression<FunctionCall> {

    private final String function;
    private final StringOrCode<Expression<?>> object;


    public FunctionCall(String function) {
        this(function, 0);
    }

    public FunctionCall(String function, int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = null;
    }

    public FunctionCall(String function, String object) {
        this(function, object, 0);
    }

    public FunctionCall(String function, String object, int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = new StringOrCode<>(object);
    }

    public FunctionCall(String function, Expression<?> object) {
        this(function, object, 0);
    }

    public FunctionCall(String function, Expression<?> object, int indentationLevel) {
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
