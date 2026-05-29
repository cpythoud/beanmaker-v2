package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class JavaClass extends Declaration<JavaClass> {

    private String extendedClass = null;
    private final List<String> implementedInterfaces = new ArrayList<>();
    private final List<String> permittedExtensions = new ArrayList<>();

    public JavaClass(String name) {
        this(name, 0);
    }

    public JavaClass(String name, int indentationLevel) {
        super("class", indentationLevel, name);
    }

    @Override
    protected JavaClass getThis() {
        return this;
    }


    public JavaClass extendsClass(String extendedClass) {
        this.extendedClass = extendedClass;
        return this;
    }

    public JavaClass implementsInterface(String implementedInterface) {
        implementedInterfaces.add(implementedInterface);
        return this;
    }

    public JavaClass implementsGenericInterface(String implementedInterface, String typeParameters) {
        implementedInterfaces.add(implementedInterface + "<" + typeParameters + ">");
        return this;
    }

    public JavaClass permitExtension(String javaClass) {
        permittedExtensions.add(javaClass);
        markAsSealed();
        return this;
    }


    @Override
    public JavaClass markAsSynchronized() {
        throw new UnsupportedOperationException("Class cannot be synchronized.");
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        appendAnnotations(buf);

        appendDeclarationStart(buf);

        buf.append("class ");
        buf.append(getName());
        buf.append(" ");

        if (extendedClass != null) {
            buf.append("extends ");
            buf.append(extendedClass);
            buf.append(" ");
        }

        if (implementedInterfaces.size() > 0) {
            buf.append("implements ");
            appendCommaSeparatedListItems(buf, implementedInterfaces);
            buf.append(" ");
        }

        if (permittedExtensions.size() > 0) {
            buf.append("permits ");
            appendCommaSeparatedListItems(buf, permittedExtensions);
            buf.append(" ");
        }

        appendContent(buf);

        buf.append("\n");

        return buf.toString();
    }

    public ConstructorDeclaration createConstructor() {
        return new ConstructorDeclaration(getName(), getIndentationLevel() + 1);
    }
}
