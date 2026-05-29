package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

public class JavaEnum extends Declaration<JavaEnum> {

    private final List<String> implementedInterfaces = new ArrayList<>();
    private final List<String> enumConstants = new ArrayList<>();

    public JavaEnum(String name) {
        this(name, 0);
    }

    public JavaEnum(String name, int indentationLevel) {
        super("class", indentationLevel, name);
    }

    @Override
    protected JavaEnum getThis() {
        return this;
    }


    public JavaEnum implementsInterface(String implementedInterface) {
        implementedInterfaces.add(implementedInterface);
        return this;
    }

    public JavaEnum implementsGenericInterface(String implementedInterface, String typeParameters) {
        implementedInterfaces.add(implementedInterface + "<" + typeParameters + ">");
        return this;
    }

    public JavaEnum addEnumConstant(String enumConstant) {
        enumConstants.add(enumConstant);
        return this;
    }


    @Override
    public JavaEnum markAsSynchronized() {
        throw new UnsupportedOperationException("Enum cannot be synchronized.");
    }


    @Override
    public String toString() {
        if (enumConstants.isEmpty())
            throw new IllegalStateException("Enum must have at least one enum constant.");

        StringBuilder buf = new StringBuilder();

        appendAnnotations(buf);

        appendDeclarationStart(buf);

        buf.append("enum ");
        buf.append(getName());
        buf.append(" ");

        if (!implementedInterfaces.isEmpty()) {
            buf.append("implements ");
            appendCommaSeparatedListItems(buf, implementedInterfaces);
            buf.append(" ");
        }

        appendContent(buf);

        buf.append("\n");

        return buf.toString();
    }

    @Override
    protected void appendContent(StringBuilder buf) {
        buf.append("{\n");

        buf.append(getTabs(getIndentationLevel() + 1));
        for (String enumConstant: enumConstants)
            buf.append(enumConstant).append(", ");
        buf.delete(buf.length() - 2, buf.length());
        if (hasContent())
            buf.append(";\n");
        buf.append("\n");

        appendCodeBlocks(buf);

        buf.append(getTabs());
        buf.append("}");
    }

    public ConstructorDeclaration createConstructor() {
        return new ConstructorDeclaration(getName(), getIndentationLevel() + 1);
    }
    
}
