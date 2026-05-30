package org.beanmaker.v2.codegen.java;

public class StaticBlock extends JavaCodeBlock<StaticBlock> {

    public StaticBlock() {
        this(0);
    }

    public StaticBlock(int indentationLevel) {
        super("static", indentationLevel);
    }

    @Override
    protected StaticBlock getThis() {
        return this;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("static ");
        appendContent(buf);
        buf.append("\n");

        return buf.toString();
    }

}
