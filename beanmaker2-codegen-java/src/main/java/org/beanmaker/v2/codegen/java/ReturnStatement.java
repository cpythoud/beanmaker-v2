package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ReturnStatement extends JavaCodeBlock<ReturnStatement> {

    private final StringOrCode<JavaCodeBlock<?>> returnedVal;
    private final Condition condition;


    public ReturnStatement() {
        super("return", 0);
        returnedVal = null;
        condition = null;
    }

    public ReturnStatement(String returnedVal) {
        this(returnedVal, 0);
    }

    public ReturnStatement(JavaCodeBlock<?> returnedVal) {
        this(returnedVal, 0);
    }

    public ReturnStatement(String returnedVal, int indentationLevel) {
        super("return", indentationLevel);
        this.returnedVal = new StringOrCode<>(returnedVal);
        condition = null;
    }

    public ReturnStatement(JavaCodeBlock<?> returnedVal, int indentationLevel) {
        super("return", indentationLevel);
        this.returnedVal = new StringOrCode<>(returnedVal);
        condition = null;
    }

    public ReturnStatement(Condition condition) {
        this(condition, 0);
    }

    public ReturnStatement(Condition condition, int indentationLevel) {
        super("return", indentationLevel);
        returnedVal = null;
        this.condition = condition;
    }


    @Override
    protected ReturnStatement getThis() {
        return this;
    }


    @Override
    public String toString() {
        if (returnedVal != null && condition != null)
            throw new IllegalStateException("At least one of returnedVal and condition must be null.");
        if (returnedVal == null && condition == null)
            return getTabs() + "return;\n";

        var buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("return ");
        if (returnedVal != null)
            buf.append(returnedVal);
        if (condition != null)
            buf.append(condition);
        buf.append(";\n");

        return buf.toString();
    }

}
