package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class OperatorExpression extends JavaCodeBlock<OperatorExpression> {

    private final StringOrCode<JavaCodeBlock> left;
    private final StringOrCode<JavaCodeBlock> right;
    private final Operator operator;

    private boolean embedded = true;
    private boolean parentheses = false;


    public static enum Operator {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY(""),
        DIVIDE("*"),
        MODULO("%");

        private final String val;

        private Operator(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }


    public OperatorExpression(final String left, final String right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final String left, final FunctionCall right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final String left, final OperatorExpression right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final FunctionCall left, final String right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final FunctionCall left, final FunctionCall right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final FunctionCall left, final OperatorExpression right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final OperatorExpression left, final String right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final OperatorExpression left, final FunctionCall right, final Operator operator) {
        this(left, right, operator, 0);
    }

    public OperatorExpression(final OperatorExpression left, final OperatorExpression right, final Operator operator) {
        this(left, right, operator, 0);
    }


    public OperatorExpression(final String left, final String right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final String left, final FunctionCall right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final String left, final OperatorExpression right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final FunctionCall left, final String right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final FunctionCall left, final FunctionCall right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final FunctionCall left, final OperatorExpression right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final OperatorExpression left, final String right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final OperatorExpression left, final FunctionCall right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
        this.operator = operator;
    }

    public OperatorExpression(final OperatorExpression left, final OperatorExpression right, final Operator operator, final int indentation) {
        super(operator.getVal(), indentation);
        this.left = new StringOrCode<JavaCodeBlock>(left);
        this.right = new StringOrCode<JavaCodeBlock>(right);
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
        final StringBuilder buf = new StringBuilder();

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
