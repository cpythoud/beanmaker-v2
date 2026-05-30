package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

public abstract class LambdaSyntaxElement<T extends LambdaSyntaxElement<T>> extends Expression<T>  {

    private final List<LambdaParameter> parameters = new ArrayList<>();

    public LambdaSyntaxElement(String keyword, int indentationLevel) {
        super(keyword, indentationLevel);
    }

    public T addLambdaParameter(String simpleParameter) {
        parameters.add(new LambdaParameter(simpleParameter));
        return getThis();
    }

    public T addLambdaParameter(FunctionArgument typedParameter) {
        parameters.add(new LambdaParameter(typedParameter));
        return getThis();
    }

    protected void appendLambdaParameters(StringBuilder buf) {
        if (parameters.isEmpty()) {
            buf.append("() -> ");
            return;
        }

        boolean needParentheses = parameters.size() > 1 || !parameters.getFirst().isSimpleParameter();
        if (needParentheses)
            buf.append("(");

        for (LambdaParameter parameter: parameters)
            buf.append(parameter).append(", ");
        buf.delete(buf.length() - 2, buf.length());

        if (needParentheses)
            buf.append(")");

        buf.append(" -> ");
    }

}
