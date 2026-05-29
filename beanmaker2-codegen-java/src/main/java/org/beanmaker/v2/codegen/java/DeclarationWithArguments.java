package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public abstract class DeclarationWithArguments<T extends DeclarationWithArguments<T>> extends Declaration<T>  {

    private final List<StringOrCode<FunctionArgument>> arguments = new ArrayList<StringOrCode<FunctionArgument>>();
    private final List<String> exceptions = new ArrayList<String>();


    public DeclarationWithArguments(final String keyword, final int indentLevel, final String name) {
        super(keyword, indentLevel, name);
    }


    public T addArgument(final String arg) {
        arguments.add(new StringOrCode<FunctionArgument>(arg));
        return getThis();
    }

    public T addArguments(final String... args) {
        for (String arg: args)
            arguments.add(new StringOrCode<FunctionArgument>(arg));
        return getThis();
    }

    public T addArgument(final FunctionArgument arg) {
        arguments.add(new StringOrCode<FunctionArgument>(arg));
        return getThis();
    }

    public T addArguments(final FunctionArgument... args) {
        for (FunctionArgument arg: args)
            arguments.add(new StringOrCode<FunctionArgument>(arg));
        return getThis();
    }

    public T addException(final String ex) {
        exceptions.add(ex);
        return getThis();
    }

    public T addExceptions(final String... exex) {
        for (String ex: exex)
            exceptions.add(ex);
        return getThis();
    }


    protected void appendArgumentList(final StringBuilder buf) {
        buf.append("(");
        if (arguments.size() > 0)
            appendCommaSeparatedListItems(buf, StringOrCode.getStrings(arguments));
        buf.append(")");
    }

    protected void appendExceptionsThrown(final StringBuilder buf) {
        if (exceptions.size() > 0) {
            buf.append(" throws ");
            appendCommaSeparatedListItems(buf, exceptions);
        }
    }


}
