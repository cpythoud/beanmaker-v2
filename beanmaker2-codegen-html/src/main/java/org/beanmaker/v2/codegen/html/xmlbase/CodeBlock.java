package org.beanmaker.v2.codegen.html.xmlbase;

/**
 * ...
 */
public class CodeBlock {
    public CodeBlock(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }

    public int getIndentationLevel() {
        return indentationLevel;
    }

    public void setIndentationLevel(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }

    public String getTabs() {
        return getTabs(indentationLevel);
    }

    public static String getTabs(int indentationLevel) {
        if (indentationLevel < 0)
            throw new IllegalArgumentException("Indentation level cannot be negative");

        return "\t".repeat(indentationLevel);
    }

    private int indentationLevel;

}
