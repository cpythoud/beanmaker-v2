package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ElseBlock extends JavaCodeBlock<ElseBlock> {

    private boolean startLine = false;


    public ElseBlock() {
        this(0);
    }

    public ElseBlock(int indentationLevel) {
        super("else", indentationLevel);
    }


    @Override
    protected ElseBlock getThis() {
        return this;
    }

    public ElseBlock atStartOfLine() {
        startLine = true;
        return getThis();
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        if (startLine)
            buf.append(getTabs());
        else
            buf.append(" ");
        buf.append("else");

        if (contentIsAOneLiner()) {
            appendOneLinerContent(buf);
        } else {
            buf.append(" ");
            appendContent(buf);
        }

        return buf.toString();
    }
}
