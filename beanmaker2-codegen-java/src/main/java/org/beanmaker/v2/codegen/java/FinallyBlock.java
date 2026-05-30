package org.beanmaker.v2.codegen.java;

public class FinallyBlock extends JavaCodeBlock<FinallyBlock> {

    public FinallyBlock() {
        this(0);
    }

    public FinallyBlock(int indentationLevel) {
        super("finally", indentationLevel);
    }

    @Override
    protected FinallyBlock getThis() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(" finally ");
        appendContent(buf);

        return buf.toString();
    }

}
