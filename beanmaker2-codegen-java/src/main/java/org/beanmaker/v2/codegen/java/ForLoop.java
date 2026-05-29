package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ForLoop extends JavaCodeBlock<ForLoop> {

    private final String loopCondition;

    public ForLoop(final String loopCondition) {
        this(loopCondition,  0);
    }

    public ForLoop(final String loopCondition, final int indentationLevel) {
        super("for", indentationLevel);
        this.loopCondition = loopCondition;
    }

    @Override
    protected ForLoop getThis() {
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

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

    @Deprecated
    public static ForLoop forAll(final String type, final String var, final String items) {
        return forAll(type, var, items, 0);
    }

    @Deprecated
    public static ForLoop forAll(final String type, final String var, final String items, final int indentationLevel) {
        return new ForLoop(type + " " + var + ": " + items, indentationLevel);
    }
}
