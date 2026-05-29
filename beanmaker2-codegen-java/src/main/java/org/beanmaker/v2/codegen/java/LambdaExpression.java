package org.beanmaker.v2.codegen.java;

public class LambdaExpression extends LambdaSyntaxElement<LambdaExpression> {

    public LambdaExpression() {
        this(0);
    }

    public LambdaExpression(int indentationLevel) {
        super("LambdaExpression", indentationLevel);
    }

    @Override
    protected LambdaExpression getThis() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        startExpression(buf);

        appendLambdaParameters(buf);
        appendOneLinerContent(buf, false);

        endExpression(buf);

        return buf.toString();
    }

}
