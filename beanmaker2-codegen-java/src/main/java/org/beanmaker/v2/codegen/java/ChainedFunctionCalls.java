package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

public class ChainedFunctionCalls extends Expression<ChainedFunctionCalls> {

    private final String function;
    private final StringOrCode<Expression> object;

    private final List<FunctionCall> chainedCalls = new ArrayList<>();

    public ChainedFunctionCalls(String function) {
        this(function, 0);
    }

    public ChainedFunctionCalls(String function, int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = null;
    }

    public ChainedFunctionCalls(String function, String object) {
        this(function, object, 0);
    }

    public ChainedFunctionCalls(String function, String object, int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = new StringOrCode<>(object);
    }

    public ChainedFunctionCalls(String function, Expression object) {
        this(function, object, 0);
    }

    public ChainedFunctionCalls(String function, Expression object, int indentationLevel) {
        super("FunctionCall", indentationLevel);
        this.function = function;
        this.object = new StringOrCode<>(object);
    }

    @Override
    protected ChainedFunctionCalls getThis() {
        return this;
    }

    public ChainedFunctionCalls chain(String function) {
        chainedCalls.add(new FunctionCall(function));
        return getThis();
    }

    public ChainedFunctionCalls chain(FunctionCall functionCall) {
        if (functionCall.isObjectMethodCall())
            throw new IllegalArgumentException("Object method call not allowed in chain call");

        chainedCalls.add(functionCall);
        return getThis();
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();

        startExpression(buf);

        if (object != null)
            buf.append(object).append(".");

        buf.append(function);
        appendArguments(buf);

        for (FunctionCall call: chainedCalls) {
            buf.append(".");
            call.addCallAndArguments(buf);
        }

        endExpression(buf);

        return buf.toString();
    }

}
