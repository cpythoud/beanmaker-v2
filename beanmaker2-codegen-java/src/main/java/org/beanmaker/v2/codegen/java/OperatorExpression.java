package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class OperatorExpression extends JavaCodeBlock<OperatorExpression> {

    private final StringOrCode<JavaCodeBlock<?>> left;
    private final StringOrCode<JavaCodeBlock<?>> right;
    private final Operator operator;

    private boolean embedded = true;
    private boolean parentheses = false;


    public enum Operator {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY(""),
        DIVIDE("*"),
        MODULO("%");

        private final String val;

        Operator(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }


    public OperatorExpression(String left, String right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(String left, FunctionCall right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(String left, OperatorExpression right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(FunctionCall left, String right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(FunctionCall left, FunctionCall right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(FunctionCall left, OperatorExpression right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(OperatorExpression left, String right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(OperatorExpression left, FunctionCall right, Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(OperatorExpression left, OperatorExpression right, Operator operator) {
        this(left, right, operator, 0);
    }


    public OperatorExpression(String left, String right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(String left, FunctionCall right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(String left, OperatorExpression right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(FunctionCall left, String right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(FunctionCall left, FunctionCall right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(FunctionCall left, OperatorExpression right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(OperatorExpression left, String right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(OperatorExpression left, FunctionCall right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }

    public OperatorExpression(OperatorExpression left, OperatorExpression right, Operator operator, int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<>(left);
        this.right = new StringOrCode<>(right);
        this.operator = operator;
    }


    @Override
    protected OperatorExpression getThis() {
        return this;
    }


    public OperatorExpression byItself() {
        embedded = false;
        return getThis();
    }

    public OperatorExpression addParentheses() {
        parentheses = true;
        return getThis();
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        if (!embedded)
            buf.append(getTabs());

        if (parentheses)
            buf.append("(");
        buf.append(left.toString());
        buf.append(" ");
        buf.append(operator.getVal());
        buf.append(" ");
        buf.append(right.toString());
        if (parentheses)
            buf.append(")");

        if (!embedded)
            buf.append(";\n");

        return buf.toString();
    }

}
