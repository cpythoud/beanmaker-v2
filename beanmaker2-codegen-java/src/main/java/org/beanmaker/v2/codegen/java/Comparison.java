package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class Comparison extends JavaCodeBlock<Comparison> {

    private final StringOrCode<Expression> lvalue;
    private final StringOrCode<Expression> rvalue;
    private final Comparator comparator;


    public enum Comparator {
        EQUAL("=="),
        NEQ("!="),
        LESS_THAN("<"),
        GREATER_THAN(">"),
        LT_EQUAL("<="),
        GT_EQUAL(">=");

        private final String val;

        private Comparator(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }


    public Comparison(final String lvalue, final String rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(final String lvalue, final String rvalue, final Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(final String lvalue, final String rvalue, final Comparator comparator, final int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<Expression>(lvalue);
        this.rvalue = new StringOrCode<Expression>(rvalue);
        this.comparator = comparator;
    }

    public Comparison(final String lvalue, final Expression rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(final String lvalue, final Expression rvalue, final Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(final String lvalue, final Expression rvalue, final Comparator comparator, final int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<Expression>(lvalue);
        this.rvalue = new StringOrCode<Expression>(rvalue);
        this.comparator = comparator;
    }

    public Comparison(final Expression lvalue, final String rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(final Expression lvalue, final String rvalue, final Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(final Expression lvalue, final String rvalue, final Comparator comparator, final int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<Expression>(lvalue);
        this.rvalue = new StringOrCode<Expression>(rvalue);
        this.comparator = comparator;
    }

    public Comparison(final Expression lvalue, final Expression rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(final Expression lvalue, final Expression rvalue, final Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(final Expression lvalue, final Expression rvalue, final Comparator comparator, final int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<Expression>(lvalue);
        this.rvalue = new StringOrCode<Expression>(rvalue);
        this.comparator = comparator;
    }


    @Override
    protected Comparison getThis() {
        return this;
    }


    @Override
    public String toString() {
        return lvalue.toString() + " " + comparator.getVal() + " " + rvalue.toString();
    }


    public static Comparison isNull(final String expression) {
        return isNull(expression, 0);
    }

    public static Comparison isNull(final Expression expression) {
        return isNull(expression, 0);
    }

    public static Comparison isNull(final String expression, final int indentationLevel) {
        return nullCheck(expression, Comparator.EQUAL, indentationLevel);
    }

    public static Comparison isNull(final Expression expression, final int indentationLevel) {
        return nullCheck(expression, Comparator.EQUAL, indentationLevel);
    }

    public static Comparison isNotNull(final String expression) {
        return isNotNull(expression, 0);
    }

    public static Comparison isNotNull(final Expression expression) {
        return isNotNull(expression, 0);
    }

    public static Comparison isNotNull(final String expression, final int indentationLevel) {
        return nullCheck(expression, Comparator.NEQ, indentationLevel);
    }

    public static Comparison isNotNull(final Expression expression, final int indentationLevel) {
        return nullCheck(expression, Comparator.NEQ, indentationLevel);
    }

    private static Comparison nullCheck(final String lvalue, final Comparator comparator, final int indentationLevel) {
        return new Comparison(lvalue, "null", comparator, indentationLevel);
    }

    private static Comparison nullCheck(final Expression lvalue, final Comparator comparator, final int indentationLevel) {
        return new Comparison(lvalue, "null", comparator, indentationLevel);
    }
}
