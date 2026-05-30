package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class ConstructorDeclaration extends DeclarationWithArguments<ConstructorDeclaration> {

    public ConstructorDeclaration(String className) {
        this(className, 0);
    }

    public ConstructorDeclaration(String className, int indentationLevel) {
        super("Constructor", indentationLevel, className);
    }

    @Override
    protected ConstructorDeclaration getThis() {
        return this;
    }


    @Override
    public ConstructorDeclaration markAsStatic() {
        throw new UnsupportedOperationException("Constructors cannot be static.");
    }

    @Override
    public ConstructorDeclaration markAsFinal() {
        throw new UnsupportedOperationException("Constructors cannot be final.");
    }

    @Override
    public ConstructorDeclaration markAsAbstract() {
        throw new UnsupportedOperationException("Constructors cannot be abstract.");
    }

    @Override
    public ConstructorDeclaration markAsSynchronized() {
        throw new UnsupportedOperationException("Constructors cannot be synchronized.");
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        appendAnnotations(buf);

        appendDeclarationStart(buf);

        buf.append(getName());

        appendArgumentList(buf);
        appendExceptionsThrown(buf);
        buf.append(" ");

        appendContent(buf);

        buf.append("\n");

        return buf.toString();
    }

}
