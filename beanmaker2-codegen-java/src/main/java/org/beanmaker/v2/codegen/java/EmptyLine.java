package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class EmptyLine extends JavaCodeBlock<EmptyLine> {

    public EmptyLine() {
        super("", 0);
    }

    @Override
    protected EmptyLine getThis() {
        return this;
    }


    @Override
    public String toString() {
        return "\n";
    }
}
