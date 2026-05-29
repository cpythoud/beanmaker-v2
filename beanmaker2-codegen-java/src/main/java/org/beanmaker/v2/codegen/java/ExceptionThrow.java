package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ExceptionThrow extends CallWithArgs<ExceptionThrow> {

    private final String exception;


    public ExceptionThrow(final String exception) {
        this(exception, 0);
    }

    public ExceptionThrow(final String exception, final int indentationLevel) {
        super("Exception", indentationLevel);
        this.exception = exception;
    }


    @Override
    protected ExceptionThrow getThis() {
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("throw new ");
        buf.append(exception);
        appendArguments(buf);
        buf.append(";\n");

        return buf.toString();
    }


    public static ExceptionThrow getThrowExpression(final String exception, final String message) {
        return getThrowExpression(exception, message, 0);
    }

    public static ExceptionThrow getThrowExpression(final String exception, final String message, final int indentationLevel) {
        return new ExceptionThrow(exception, indentationLevel).addArgument(Strings.quickQuote(message));
    }
}
