package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class WhileBlock extends ConditionalBlock<WhileBlock> {

    public WhileBlock(Condition condition) {
        this(condition, 0);
    }

    public WhileBlock(Condition condition, int indentationLevel) {
        super("while", indentationLevel, condition);
    }


    @Override
    protected WhileBlock getThis() {
        return this;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("while");
        appendCondition(buf);

        if (contentIsAOneLiner())
            appendOneLinerContent(buf);
        else {
            buf.append(" ");
            appendContent(buf);
        }

        return buf.toString();
    }

}
