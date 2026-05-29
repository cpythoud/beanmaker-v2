package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class DoWhileBlock extends ConditionalBlock<DoWhileBlock> {

    public DoWhileBlock(final Condition condition) {
        this(condition, 0);
    }

    public DoWhileBlock(final Condition condition, final int indentationLevel) {
        super("do", indentationLevel, condition);
    }


    @Override
    protected DoWhileBlock getThis() {
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("do");

        if (contentIsAOneLiner())
            appendOneLinerContent(buf);
        else {
            buf.append(" ");
            appendContent(buf);
        }

        buf.append("while");
        appendCondition(buf);

        buf.append("\n");

        return buf.toString();
    }
}
