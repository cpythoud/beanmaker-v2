package org.beanmaker.v2.codegen.java;

public class ForEach extends JavaCodeBlock<ForEach> {

    private final String itemClass;
    private final String item;
    private final StringOrCode<FunctionCall> items;

    public ForEach(final String itemClass, final String item, final String items) {
        this(itemClass, item, items, 0);
    }

    public ForEach(final String itemClass, final String item, final String items, final int indentationLevel) {
        super("for", indentationLevel);
        this.itemClass = itemClass;
        this.item = item;
        this.items = new StringOrCode<FunctionCall>(items);
    }

    public ForEach(final String itemClass, final String item, final FunctionCall items) {
        this(itemClass, item, items, 0);
    }

    public ForEach(final String itemClass, final String item, final FunctionCall items, final int indentationLevel) {
        super("for", indentationLevel);
        this.itemClass = itemClass;
        this.item = item;
        this.items = new StringOrCode<FunctionCall>(items);
    }

    @Override
    protected ForEach getThis() {
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("for (");
        buf.append(itemClass);
        buf.append(" ");
        buf.append(item);
        buf.append(": ");
        buf.append(items);
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
