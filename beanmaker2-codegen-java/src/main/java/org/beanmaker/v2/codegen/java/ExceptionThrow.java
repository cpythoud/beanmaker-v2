package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ExceptionThrow extends CallWithArgs<ExceptionThrow> {

    private final String exception;


    public ExceptionThrow(String exception) {
        this(exception, 0);
    }

    public ExceptionThrow(String exception, int indentationLevel) {
        super("Exception", indentationLevel);
        this.exception = exception;
    }


    @Override
    protected ExceptionThrow getThis() {
        return this;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append("throw new ");
        buf.append(exception);
        appendArguments(buf);
        buf.append(";\n");

        return buf.toString();
    }


    public static ExceptionThrow getThrowExpression(String exception, String message) {
        return getThrowExpression(exception, message, 0);
    }

    public static ExceptionThrow getThrowExpression(String exception, String message, int indentationLevel) {
        return new ExceptionThrow(exception, indentationLevel).addArgument(Strings.quickQuote(message));
    }

}
