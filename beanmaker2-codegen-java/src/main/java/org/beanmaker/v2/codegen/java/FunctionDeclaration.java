package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class FunctionDeclaration extends DeclarationWithArguments<FunctionDeclaration> {

    private final String returnType;

    private boolean emptyBody = false;


    public FunctionDeclaration(String name) {
        this(name, "void", 0);
    }

    public FunctionDeclaration(String name, String returnType) {
        this(name, returnType, 0);
    }

    public FunctionDeclaration(String name, String returnType, int indentationLevel) {
        super("Function", indentationLevel, name);
        this.returnType = returnType;
    }

    public FunctionDeclaration(String name, GenericType returnType) {
        this(name, returnType, 0);
    }

    public FunctionDeclaration(String name, GenericType returnType, int indentationLevel) {
        super("Function", indentationLevel, name);
        this.returnType = returnType.toString();
    }

    public FunctionDeclaration emptyBody() {
        emptyBody = true;
        return getThis();
    }


    @Override
    protected FunctionDeclaration getThis() {
        return this;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        appendAnnotations(buf);

        appendDeclarationStart(buf);

        buf.append(returnType);
        buf.append(" ");

        buf.append(getName());

        appendArgumentList(buf);
        appendExceptionsThrown(buf);
        if (emptyBody)
            buf.append(";");
        else {
            buf.append(" ");
            appendContent(buf);
        }

        buf.append("\n");

        return buf.toString();
    }


    public static FunctionDeclaration getter(String type, String var) {
        return getter(type, var, 0);
    }

    public static FunctionDeclaration getter(String type, String var, int indentLevel) {
        return new FunctionDeclaration("get" + Strings.capitalize(var), type, indentLevel).addContent(
                new ReturnStatement(var)
        );
    }
    
}
