package org.beanmaker.v2.codegen.java;

public class CatchBlock extends DeclarationWithArguments<CatchBlock> {

    public CatchBlock(final FunctionArgument functionArgument) {
        this(functionArgument, 0);
    }

    public CatchBlock(final String functionArgument) {
        this(functionArgument, 0);
    }

    public CatchBlock(final FunctionArgument functionArgument, final int indentLevel) {
        super("catch", indentLevel, "catch");
        super.addArgument(functionArgument);
    }

    public CatchBlock(final String functionArgument, final int indentLevel) {
        super("catch", indentLevel, "catch");
        super.addArgument(functionArgument);
    }

    @Override
    protected CatchBlock getThis() {
        return this;
    }

    @Override
    public CatchBlock addArgument(String arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock addArguments(String... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock addArgument(FunctionArgument arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock addArguments(FunctionArgument... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock addException(String ex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock addExceptions(String... exex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock visibility(Visibility visibility) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock markAsAbstract() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock markAsFinal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock markAsStatic() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock markAsSynchronized() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CatchBlock annotate(String annotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(" catch ");

        appendArgumentList(buf);
        buf.append(" ");

        appendContent(buf);

        buf.append("\n");

        return buf.toString();
    }
}
