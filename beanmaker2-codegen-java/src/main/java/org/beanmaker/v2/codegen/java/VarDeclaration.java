package org.beanmaker.v2.codegen.java;

import java.util.Arrays;
import java.util.List;

/**
 * ...
 */
public class VarDeclaration extends Declaration<VarDeclaration> {

    private final String type;
    private final StringOrCode<Expression<?>> value;


    public VarDeclaration(String type, String name) {
        super("Variable", 0, name);
        visibility(Visibility.NONE);
        this.type = type;
        this.value = null;
    }

    public VarDeclaration(String type, String name, String value) {
        this(type, name, value, Visibility.NONE);
    }

    public VarDeclaration(String type, String name, String value, Visibility visibility) {
        this(type, name, value, visibility, 0);
    }

    public VarDeclaration(String type, String name, String value, Visibility visibility, int indentationLevel) {
        super("Variable", indentationLevel, name);
        visibility(visibility);
        this.type = type;
        this.value = new StringOrCode<>(value);
    }

    public VarDeclaration(String type, String name, Expression<?> value) {
        this(type, name, value, Visibility.NONE);
    }

    public VarDeclaration(String type, String name, Expression<?> value, Visibility visibility) {
        this(type, name, value, visibility, 0);
    }

    public VarDeclaration(String type, String name, Expression<?> value, Visibility visibility, int indentationLevel) {
        super("Variable", indentationLevel, name);
        visibility(visibility);
        this.type = type;
        this.value = new StringOrCode<>(value);
    }


    @Override
    protected VarDeclaration getThis() {
        return this;
    }

    @Override
    public VarDeclaration markAsAbstract() {
        throw new UnsupportedOperationException("Field or variable cannot be abstract.");
    }

    @Override
    public VarDeclaration markAsSynchronized() {
        throw new UnsupportedOperationException("Field or variable cannot be synchronized.");
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        appendAnnotations(buf);

        appendDeclarationStart(buf);

        buf.append(type);
        buf.append(" ");

        buf.append(getName());

        if (value != null) {
            buf.append(" = ");
            buf.append(value);
        }

        buf.append(";\n");

        return buf.toString();
    }


    public static VarDeclaration declareAndInit(String type, String name) {
        return new VarDeclaration(type, name, new ObjectCreation(type));
    }

    public static VarDeclaration declareAndInitFinal(String type, String name) {
        return declareAndInit(type, name).markAsFinal();
    }


    public static VarDeclaration createListDeclaration(String type, String name) {
        return createListDeclaration(type, name, Visibility.NONE);
    }

    public static VarDeclaration createListDeclaration(String type, String name, Visibility visibility) {
        return createListDeclaration(type, name, visibility, false);
    }

    public static VarDeclaration createListDeclaration(String type, String name, Visibility visibility, boolean repeatType) {
        return createListDeclaration(type, name, visibility, repeatType, 0);
    }

    public static VarDeclaration createListDeclaration(String type, String name, Visibility visibility, boolean repeatType, int indentationLevel) {
        return createGenericContainerDeclaration("List", "ArrayList", type, name, visibility, repeatType, indentationLevel);
    }


    public static VarDeclaration createGenericContainerDeclaration(
            String genericType,
            String type,
            String name)
    {
        return createGenericContainerDeclaration(genericType, type, name, Visibility.NONE);
    }

    public static VarDeclaration createGenericContainerDeclaration(
            String genericType,
            String type,
            String name,
            Visibility visibility)
    {
        return createGenericContainerDeclaration(genericType, type, name, visibility, 0);
    }

    public static VarDeclaration createGenericContainerDeclaration(
            String genericType,
            String type,
            String name,
            Visibility visibility,
            int indentationLevel)
    {
        return createGenericContainerDeclaration(genericType, genericType, type, name, visibility, false, indentationLevel);
    }

    public static VarDeclaration createGenericContainerDeclaration(
            String genericInterface,
            String genericType,
            String type,
            String name)
    {
        return createGenericContainerDeclaration(genericInterface, genericType, type, name, Visibility.NONE);
    }

    public static VarDeclaration createGenericContainerDeclaration(
            String genericInterface,
            String genericType,
            String type,
            String name,
            Visibility visibility)
    {
        return createGenericContainerDeclaration(genericInterface, genericType, type, name, visibility, false);
    }

    public static VarDeclaration createGenericContainerDeclaration(
            String genericInterface,
            String genericType,
            String type,
            String name,
            Visibility visibility,
            boolean repeatType)
    {
        return createGenericContainerDeclaration(genericInterface, genericType, type, name, visibility, repeatType, 0);
    }

    public static VarDeclaration createGenericContainerDeclaration(
            String genericInterface,
            String genericType,
            String type,
            String name,
            Visibility visibility,
            boolean repeatType,
            int indentationLevel)
    {
        return new VarDeclaration(
                getParametrizedType(genericInterface, type),
                name,
                new ObjectCreation(repeatType ? getParametrizedType(genericType, type) : getParametrizedType(genericType)), visibility, indentationLevel);
    }

    public static String getParametrizedType(String genericType) {
        return genericType + "<>";
    }

    public static String getParametrizedType(String genericType, String concreteType) {
        return genericType + "<" + concreteType + ">";
    }

    public static String getParametrizedType(String genericType, String... concreteTypes) {
        return getParametrizedType(genericType, Arrays.asList(concreteTypes));
    }

    public static String getParametrizedType(String genericType, List<String> concreteTypes) {
        if (concreteTypes.isEmpty())
            throw new IllegalArgumentException("At least one concrete type must be specified");

        StringBuilder types = new StringBuilder();
        for (String type: concreteTypes)
            types.append(type).append(", ");
        types.delete(types.length() - 2, types.length());

        return getParametrizedType(genericType, types.toString());
    }

}
