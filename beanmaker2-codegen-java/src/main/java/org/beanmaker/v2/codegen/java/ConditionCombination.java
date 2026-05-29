package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
class ConditionCombination {

    public enum Type {
        AND(" && "),
        OR(" || ");

        private final String operator;

        Type(String operator) {
            this.operator = operator;
        }

        public String toJavaOperator() {
            return operator;
        }
    }

    private final Condition condition;
    private final Type type;

    public ConditionCombination(final Condition condition, final Type type) {
        this.condition = condition;
        this.type = type;
    }

    public Condition getCondition() {
        return condition;
    }

    public Type getType() {
        return type;
    }
}
