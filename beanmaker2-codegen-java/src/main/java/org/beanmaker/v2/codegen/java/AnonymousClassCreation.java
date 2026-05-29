package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class AnonymousClassCreation extends Expression<AnonymousClassCreation> {

    private final String className;

    private JavaCodeBlock<?> context = null;
    private int extraContextIndentation = 0;

    public AnonymousClassCreation(String className) {
        this(className, 0);
    }

    public AnonymousClassCreation(String className, int indentationLevel) {
        super("AnonymousClass", indentationLevel);
        this.className = className;
    }


    @Override
    protected AnonymousClassCreation getThis() {
        return this;
    }

    public AnonymousClassCreation setContext(JavaCodeBlock<?> context) {
        return setContext(context, 0);
    }

    public AnonymousClassCreation setContext(JavaCodeBlock<?> context, int extraContextIndentation) {
        this.context = context;
        this.extraContextIndentation = extraContextIndentation;
        return getThis();
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        if (context != null)
            setIndentationLevel(context.getIndentationLevel() + 1 + extraContextIndentation);

        buf.append("new ");
        buf.append(className);
        buf.append("() ");

        appendContent(buf);

        return buf.toString();
    }
}
