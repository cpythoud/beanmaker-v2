package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ...
 */
public abstract class DeclarationWithArguments<T extends DeclarationWithArguments<T>> extends Declaration<T>  {

    private final List<StringOrCode<FunctionArgument>> arguments = new ArrayList<>();
    private final List<String> exceptions = new ArrayList<>();


    public DeclarationWithArguments(String keyword, int indentLevel, String name) {
        super(keyword, indentLevel, name);
    }


    public T addArgument(String arg) {
        arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArguments(String... args) {
        for (String arg: args)
            arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArgument(FunctionArgument arg) {
        arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArguments(FunctionArgument... args) {
        for (FunctionArgument arg: args)
            arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addException(String ex) {
        exceptions.add(ex);
        return getThis();
    }

    public T addExceptions(String... exex) {
        Collections.addAll(exceptions, exex);
        return getThis();
    }


    protected void appendArgumentList(StringBuilder buf) {
        buf.append("(");
        if (!arguments.isEmpty())
            appendCommaSeparatedListItems(buf, StringOrCode.getStrings(arguments));
        buf.append(")");
    }

    protected void appendExceptionsThrown(StringBuilder buf) {
        if (!exceptions.isEmpty()) {
            buf.append(" throws ");
            appendCommaSeparatedListItems(buf, exceptions);
        }
    }


}
