package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class LineOfCode extends JavaCodeBlock<LineOfCode> {

    private final String code;


    public LineOfCode(String code) {
        this(code, 0);
    }

    public LineOfCode(String code, int indentationLevel) {
        super("", indentationLevel);
        this.code = code;
    }

    @Override
    protected LineOfCode getThis() {
        return this;
    }


    @Override
    public String toString() {
        return getTabs() + code + "\n";
    }


    public static LineOfCode throwException(String exception) {
        return throwException(exception, "");
    }

    public static LineOfCode throwException(String exception, String argument) {
        return new LineOfCode("throw new " + exception + "(" + argument + ");");
    }

}
