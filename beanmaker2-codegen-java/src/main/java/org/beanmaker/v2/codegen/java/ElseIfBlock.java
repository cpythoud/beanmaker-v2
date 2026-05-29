package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ElseIfBlock extends LogicBranchBlock<ElseIfBlock> {

    private boolean startLine = false;
    private boolean moreElsesToCome = false;

    public ElseIfBlock(final Condition condition) {
        this(condition, 0);
    }

    public ElseIfBlock(final Condition condition, final int indentationLevel) {
        super("else if", indentationLevel, condition);
    }


    @Override
    protected ElseIfBlock getThis() {
        return this;
    }

    public ElseIfBlock atStartOfLine() {
        startLine = true;
        return getThis();
    }

    public ElseIfBlock moreElsesToCome() {
        moreElsesToCome = true;
        return getThis();
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        if (startLine)
            buf.append(getTabs());
        else
            buf.append(" ");
        buf.append("else if");
        appendCondition(buf);

        if (contentIsSuitableForOneLiner(moreElsesToCome)) {
            appendOneLinerContent(buf);
        } else {
            buf.append(" ");
            appendContent(buf);
        }

        return buf.toString();
    }
}
