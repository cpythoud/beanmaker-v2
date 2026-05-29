package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public abstract class CallWithArgs<T extends CallWithArgs<T>> extends JavaCodeBlock<T> {

    private final List<StringOrCode<JavaCodeBlock<?>>> arguments = new ArrayList<>();


    public CallWithArgs(String keyword, int indentationLevel) {
        super(keyword, indentationLevel);
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

    public T addArgument(Expression<?> arg) {
        arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArguments(Expression<?>... args) {
        for (var arg: args)
            arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArgument(OperatorExpression arg) {
        arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArguments(OperatorExpression... args) {
        for (OperatorExpression arg: args)
            arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArgument(Comparison arg) {
        arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArguments(Comparison... args) {
        for (Comparison arg: args)
            arguments.add(new StringOrCode<>(arg));
        return getThis();
    }

    public T addArgument(Condition arg) {
        arguments.add(new StringOrCode<>(arg.toString()));
        return getThis();
    }

    public T addArguments(Condition... args) {
        for (Condition arg: args)
            arguments.add(new StringOrCode<>(arg.toString()));
        return getThis();
    }


    protected void appendArguments(StringBuilder buf) {
        buf.append("(");
        if (!arguments.isEmpty())
            appendCommaSeparatedListItems(buf, StringOrCode.getStrings(arguments));
        buf.append(")");
    }
}
