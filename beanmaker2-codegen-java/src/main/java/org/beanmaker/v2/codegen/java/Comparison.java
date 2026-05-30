package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class Comparison extends JavaCodeBlock<Comparison> {

    private final StringOrCode<Expression<?>> lvalue;
    private final StringOrCode<Expression<?>> rvalue;
    private final Comparator comparator;


    public enum Comparator {
        EQUAL("=="),
        NEQ("!="),
        LESS_THAN("<"),
        GREATER_THAN(">"),
        LT_EQUAL("<="),
        GT_EQUAL(">=");

        private final String val;

        Comparator(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }


    public Comparison(String lvalue, String rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(String lvalue, String rvalue, Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(String lvalue, String rvalue, Comparator comparator, int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
        this.comparator = comparator;
    }

    public Comparison(String lvalue, Expression<?> rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(String lvalue, Expression<?> rvalue, Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(String lvalue, Expression<?> rvalue, Comparator comparator, int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
        this.comparator = comparator;
    }

    public Comparison(Expression<?> lvalue, String rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(Expression<?> lvalue, String rvalue, Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(Expression<?> lvalue, String rvalue, Comparator comparator, int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
        this.comparator = comparator;
    }

    public Comparison(Expression<?> lvalue, Expression<?> rvalue) {
        this(lvalue, rvalue, Comparator.EQUAL, 0);
    }

    public Comparison(Expression<?> lvalue, Expression<?> rvalue, Comparator comparator) {
        this(lvalue, rvalue, comparator, 0);
    }

    public Comparison(Expression<?> lvalue, Expression<?> rvalue, Comparator comparator, int indentationLevel) {
        super(comparator.getVal(), indentationLevel);
        this.lvalue = new StringOrCode<>(lvalue);
        this.rvalue = new StringOrCode<>(rvalue);
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


    public static Comparison isNull(String expression) {
        return isNull(expression, 0);
    }

    public static Comparison isNull(Expression<?> expression) {
        return isNull(expression, 0);
    }

    public static Comparison isNull(String expression, int indentationLevel) {
        return nullCheck(expression, Comparator.EQUAL, indentationLevel);
    }

    public static Comparison isNull(Expression<?> expression, int indentationLevel) {
        return nullCheck(expression, Comparator.EQUAL, indentationLevel);
    }

    public static Comparison isNotNull(String expression) {
        return isNotNull(expression, 0);
    }

    public static Comparison isNotNull(Expression<?> expression) {
        return isNotNull(expression, 0);
    }

    public static Comparison isNotNull(String expression, int indentationLevel) {
        return nullCheck(expression, Comparator.NEQ, indentationLevel);
    }

    public static Comparison isNotNull(Expression<?> expression, int indentationLevel) {
        return nullCheck(expression, Comparator.NEQ, indentationLevel);
    }

    private static Comparison nullCheck(String lvalue, Comparator comparator, int indentationLevel) {
        return new Comparison(lvalue, "null", comparator, indentationLevel);
    }

    private static Comparison nullCheck(Expression<?> lvalue, Comparator comparator, int indentationLevel) {
        return new Comparison(lvalue, "null", comparator, indentationLevel);
    }

}
