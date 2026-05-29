package org.beanmaker.v2.codegen.java;

public class TryBlock extends JavaCodeBlock<TryBlock> {

    private CatchBlock catchBlock;
    private FinallyBlock finallyBlock;

    public TryBlock() {
        this(0);
    }

    public TryBlock(int indentationLevel) {
        super("try", indentationLevel);
    }

    @Override
    protected TryBlock getThis() {
        return this;
    }

    public TryBlock addCatchBlock(CatchBlock catchBlock) {
        this.catchBlock = catchBlock;
        return this;
    }

    public TryBlock addFinallyBlock(FinallyBlock finallyBlock) {
        this.finallyBlock = finallyBlock;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(getTabs()).append("try ");
        appendContent(buf);

        if (catchBlock != null) {
            catchBlock.setIndentationLevel(getIndentationLevel());
            buf.append(catchBlock);
        }

        if (finallyBlock != null) {
            finallyBlock.setIndentationLevel(getIndentationLevel());
            buf.append(finallyBlock);
        }

        return buf.toString();
    }

}
