package org.beanmaker.v2.codegen;

public class ExtraField {

    private final String type;
    private final String name;
    private final String initializationExpression;
    private final boolean isFinal;
    private final String requiredImport;
    private final String secondaryRequiredImport;
    private final String ternaryRequiredImport;

    public ExtraField(
            String type,
            String name,
            String initializationExpression,
            boolean isFinal,
            String requiredImport,
            String secondaryRequiredImport,
            String ternaryRequiredImport)
    {
        this.type = type;
        this.name = name;
        this.initializationExpression = initializationExpression;
        this.isFinal = isFinal;
        this.requiredImport = requiredImport;
        this.secondaryRequiredImport = secondaryRequiredImport;
        this.ternaryRequiredImport = ternaryRequiredImport;
    }

    public ExtraField(
            String type,
            String name,
            String initializationExpression,
            boolean isFinal,
            String requiredImport,
            String secondaryRequiredImport)
    {
        this(type, name, initializationExpression, isFinal, requiredImport, secondaryRequiredImport, null);
    }

    public ExtraField(
            String type,
            String name,
            String initializationExpression,
            boolean isFinal,
            String requiredImport)
    {
        this(type, name, initializationExpression, isFinal, requiredImport, null);
    }

    public ExtraField(String type, String name, String initializationExpression, boolean isFinal) {
        this(type, name, initializationExpression, isFinal, null);
    }

    public ExtraField(String type, String name,  boolean isFinal) {
        this(type, name, null, isFinal, null);
    }

    public ExtraField(String type, String name) {
        this(type, name, false);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getInitializationExpression() {
        return initializationExpression;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public String getRequiredImport() {
        return requiredImport;
    }

    public boolean requiresImport() {
        return requiredImport != null;
    }

    public String getSecondaryRequiredImport() {
        return secondaryRequiredImport;
    }

    public boolean requiresSecondaryImport() {
        return secondaryRequiredImport != null;
    }

    public String getTernaryRequiredImport() {
        return ternaryRequiredImport;
    }

    public boolean requiresTernaryImport() {
        return ternaryRequiredImport != null;
    }

    public boolean requiresAnyImport() {
        return requiresImport() || requiresSecondaryImport() || requiresTernaryImport();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("protected ");

        if (isFinal)
            buf.append("final ");

        buf.append(type).append(" ").append(name);

        if (initializationExpression != null)
            buf.append(" = ").append(initializationExpression).append(";");

        return buf.toString();
    }

}
