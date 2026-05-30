package org.beanmaker.v2.codegen.java;

public class CatchBlock extends DeclarationWithArguments<CatchBlock> {

    public CatchBlock(FunctionArgument functionArgument) {
        this(functionArgument, 0);
    }

    public CatchBlock(String functionArgument) {
        this(functionArgument, 0);
    }

    public CatchBlock(FunctionArgument functionArgument, int indentLevel) {
        super("catch", indentLevel, "catch");
        super.addArgument(functionArgument);
    }

    public CatchBlock(String functionArgument, int indentLevel) {
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
        StringBuilder buf = new StringBuilder();

        buf.append(" catch ");

        appendArgumentList(buf);
        buf.append(" ");

        appendContent(buf);

        buf.append("\n");

        return buf.toString();
    }

}
