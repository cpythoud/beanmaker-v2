package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class LineOfCode extends JavaCodeBlock<LineOfCode> {

    private final String code;


    public LineOfCode(final String code) {
        this(code, 0);
    }

    public LineOfCode(final String code, final int indentationLevel) {
        super("", indentationLevel);
        this.code = code;
    }

    @Override
    protected LineOfCode getThis() {
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append(getTabs());
        buf.append(code);
        buf.append("\n");

        return buf.toString();
    }


    public static LineOfCode throwException(final String exception) {
        return throwException(exception, "");
    }

    public static LineOfCode throwException(final String exception, final String argument) {
        return new LineOfCode("throw new " + exception + "(" + argument + ");");
    }
}
