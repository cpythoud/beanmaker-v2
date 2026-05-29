package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class Assignment extends JavaCodeBlock<Assignment> {

    private final StringOrCode<FunctionCall> lvalue;
    private final StringOrCode<Expression<?>> rvalue;
    private final OperatorExpression altRvalue;

    private boolean embedded = false;


    public Assignment(String lvalue, String rvalue) {
        this(lvalue, rvalue, 0);
    }

    public Assignment(String lvalue, String rvalue, int indentationLevel) {
        super("=", indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
        altRvalue = null;
    }

    public Assignment(FunctionCall lvalue, String rvalue) {
        this(lvalue, rvalue, 0);
    }

    public Assignment(FunctionCall lvalue, String rvalue, int indentationLevel) {
        super("=", indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
        altRvalue = null;
    }

    public Assignment(String lvalue, Expression<?> rvalue) {
        this(lvalue, rvalue, 0);
    }

    public Assignment(String lvalue, Expression<?> rvalue, int indentationLevel) {
        super("=", indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
        altRvalue = null;
    }

    public Assignment(FunctionCall lvalue, Expression<?> rvalue) {
        this(lvalue, rvalue, 0);
    }

    public Assignment(FunctionCall lvalue, Expression<?> rvalue, int indentationLevel) {
        super("=", indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
        altRvalue = null;
    }

    public Assignment(String lvalue, OperatorExpression altRvalue) {
        this(lvalue, altRvalue, 0);
    }

    public Assignment(String lvalue, OperatorExpression altRvalue, int indentationLevel) {
        super("=", indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        rvalue = null;
        this.altRvalue = altRvalue;
    }

    public Assignment(FunctionCall lvalue, OperatorExpression altRvalue) {
        this(lvalue, altRvalue, 0);
    }

    public Assignment(FunctionCall lvalue, OperatorExpression altRvalue, int indentationLevel) {
        super("=", indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        rvalue = null;
        this.altRvalue = altRvalue;
    }

    public Assignment(String lvalue, Condition altRvalue) {
        this(lvalue, altRvalue, 0);
    }

    public Assignment(String lvalue, Condition altRvalue, int indentationLevel) {
        this(lvalue, altRvalue.toString(), indentationLevel);
    }

    public Assignment(FunctionCall lvalue, Condition altRvalue) {
        this(lvalue, altRvalue, 0);
    }

    public Assignment(FunctionCall lvalue, Condition altRvalue, int indentationLevel) {
        this(lvalue, altRvalue.toString(), indentationLevel);
    }


    @Override
    protected Assignment getThis() {
        return this;
    }

    public Assignment embedded() {
        embedded = true;
        return getThis();
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        if (!embedded)
            buf.append(getTabs());

        buf.append(lvalue.toString());
        buf.append(" = ");
        if (rvalue != null)
            buf.append(rvalue.toString());
        else
            buf.append(altRvalue.toString());

        if (!embedded)
            buf.append(";\n");

        return buf.toString();
    }
}
