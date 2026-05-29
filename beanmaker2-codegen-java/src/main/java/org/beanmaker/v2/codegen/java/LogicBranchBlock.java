package org.beanmaker.v2.codegen.java;

/**
 * ...
 */

public abstract class LogicBranchBlock<T extends LogicBranchBlock<T>> extends ConditionalBlock<T> {

    private boolean forceBrackets = false;

    public LogicBranchBlock(String keyword, int indentationLevel, Condition condition) {
        super(keyword, indentationLevel, condition);
    }

    public T forceBrackets(boolean forceBrackets) {
        this.forceBrackets = forceBrackets;
        return getThis();
    }

    // TODO: re-analyse workings of function
    protected boolean contentIsSuitableForOneLiner(boolean moreElses) {
        if (forceBrackets)
            return false;

        if (!contentIsAOneLiner())
            return false;

        if (!moreElses)
            return true;

        return !contentContainsBlock("if");
    }

}
