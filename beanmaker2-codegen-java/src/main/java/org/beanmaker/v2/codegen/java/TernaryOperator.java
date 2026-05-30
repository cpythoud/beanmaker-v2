package org.beanmaker.v2.codegen.java;

public class TernaryOperator extends Expression<TernaryOperator> {

    private final Condition condition;
    private final StringOrCode<Expression<?>> trueResult;
    private final StringOrCode<Expression<?>> falseResult;


    public TernaryOperator(Condition condition, Expression<?> trueResult, Expression<?> falseResult) {
        super("Ternary", 0);
        this.condition = condition;
        this.trueResult = new StringOrCode<>(trueResult);
        this.falseResult = new StringOrCode<>(falseResult);
    }

    public TernaryOperator(Condition condition, String trueResult, Expression<?> falseResult) {
        super("Ternary", 0);
        this.condition = condition;
        this.trueResult = new StringOrCode<>(trueResult);
        this.falseResult = new StringOrCode<>(falseResult);
    }

    public TernaryOperator(Condition condition, Expression<?> trueResult, String falseResult) {
        super("Ternary", 0);
        this.condition = condition;
        this.trueResult = new StringOrCode<>(trueResult);
        this.falseResult = new StringOrCode<>(falseResult);
    }

    public TernaryOperator(Condition condition, String trueResult, String falseResult) {
        super("Ternary", 0);
        this.condition = condition;
        this.trueResult = new StringOrCode<>(trueResult);
        this.falseResult = new StringOrCode<>(falseResult);
    }


    @Override
    protected TernaryOperator getThis() {
        return this;
    }


    @Override
    public String toString() {
        return condition.toString() + " ? " + trueResult.toString() + " : " + falseResult.toString();
    }

}
