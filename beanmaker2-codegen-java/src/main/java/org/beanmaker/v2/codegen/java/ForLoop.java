package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ForLoop extends JavaCodeBlock<ForLoop> {

    private final String loopCondition;

    public ForLoop(String loopCondition) {
        this(loopCondition,  0);
    }

    public ForLoop(String loopCondition, int indentationLevel) {
        super("for", indentationLevel);
        this.loopCondition = loopCondition;
    }

    @Override
    protected ForLoop getThis() {
        return this;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("for (");
        buf.append(loopCondition);
        buf.append(")");

        if (contentIsAOneLiner()) {
            appendOneLinerContent(buf);
        } else {
            buf.append(" ");
            appendContent(buf);
            buf.append("\n");
        }

        return buf.toString();
    }

}
