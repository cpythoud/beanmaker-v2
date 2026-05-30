package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class DoWhileBlock extends ConditionalBlock<DoWhileBlock> {

    public DoWhileBlock(Condition condition) {
        this(condition, 0);
    }

    public DoWhileBlock(Condition condition, int indentationLevel) {
        super("do", indentationLevel, condition);
    }


    @Override
    protected DoWhileBlock getThis() {
        return this;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

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
