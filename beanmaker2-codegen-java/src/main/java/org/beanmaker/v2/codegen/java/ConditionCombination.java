package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
record ConditionCombination(Condition condition, Type type) {

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

}
