package org.beanmaker.v2.codegen.java;

public class Lambda extends LambdaSyntaxElement<Lambda> {

    public Lambda() {
        this(0);
    }

    public Lambda(int indentationLevel) {
        super("Lambda", indentationLevel);
    }

    @Override
    protected Lambda getThis() {
        return this;
    }

    @Override
    public String toString() {
        setIndentationLevel(getIndentationLevel() + 2);  // ! rule of thumb, we'll see if that needs some adjustment after some use
        StringBuilder buf = new StringBuilder();

        startExpression(buf);

        appendLambdaParameters(buf);
        appendContent(buf);

        endExpression(buf);

        return buf.toString();
    }

}
