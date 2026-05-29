package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public class FunctionArgument {

    private final String name;
    private final String type;
    private final boolean isFinal;

    public FunctionArgument(String type, String name) {
        this(type, name, false);
    }

    public FunctionArgument(String type, String name, boolean isFinal) {
        this.type = type;
        this.name = name;
        this.isFinal = isFinal;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        if (isFinal)
            buf.append("final ");
        buf.append(type);
        buf.append(" ");
        buf.append(name);

        return buf.toString();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }

}
