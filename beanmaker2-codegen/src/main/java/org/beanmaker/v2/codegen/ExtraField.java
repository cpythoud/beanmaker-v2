package org.beanmaker.v2.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ExtraField {

    private final String type;
    private final String name;
    private final String initializationExpression;
    private final boolean isFinal;
    private final List<String> requiredImports;

    public static class Builder {
        private final String type;
        private final String name;
        private String initializationExpression;
        private boolean isFinal;
        private final Set<String> requiredImports = new LinkedHashSet<>();

        private Builder(String type, String name) {
            this.type = type;
            this.name = name;
        }

        private Builder(ExtraField extraField) {
            type = extraField.type;
            name = extraField.name;
            initializationExpression = extraField.initializationExpression;
            isFinal = extraField.isFinal;
            requiredImports.addAll(extraField.requiredImports);
        }

        public Builder initializationExpression(String initializationExpression) {
            this.initializationExpression = initializationExpression;
            return this;
        }

        public Builder isFinal(boolean isFinal) {
            this.isFinal = isFinal;
            return this;
        }

        public Builder addImports(String... imports) {
            return addImports(Arrays.asList(imports));
        }

        public Builder addImports(List<String> imports) {
            requiredImports.addAll(imports);
            return this;
        }

        public Builder addImport(String importData) {
            if (importData != null)
                requiredImports.add(importData);
            return this;
        }

        public ExtraField create() {
            return new ExtraField(type, name, initializationExpression, isFinal, requiredImports);
        }
    }

    private ExtraField(String type, String name, String initializationExpression, boolean isFinal, Set<String> requiredImports) {
        this.type = type;
        this.name = name;
        this.initializationExpression = initializationExpression;
        this.isFinal = isFinal;
        this.requiredImports = new ArrayList<>(requiredImports);
    }

    public static Builder builder(String type, String name) {
        return new Builder(type, name);
    }

    public static Builder builder(ExtraField extraField) {
        return new Builder(extraField);
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

    public List<String> getRequiredImports() {
        return Collections.unmodifiableList(requiredImports);
    }

    @Deprecated
    public String getRequiredImport() {
        if (requiredImports.isEmpty())
            return null;

        return requiredImports.get(0);
    }

    public boolean requiresImport() {
        return !requiredImports.isEmpty();
    }

    @Deprecated
    public String getSecondaryRequiredImport() {
        if (requiredImports.size() < 2)
            return null;

        return requiredImports.get(1);
    }

    @Deprecated
    public boolean requiresSecondaryImport() {
        return requiredImports.size() > 1;
    }

    @Deprecated
    public String getTernaryRequiredImport() {
        if (requiredImports.size() < 3)
            return null;

        return requiredImports.get(2);
    }

    @Deprecated
    public boolean requiresTernaryImport() {
        return requiredImports.size() > 2;
    }

    @Deprecated
    public boolean requiresAnyImport() {
        return !requiredImports.isEmpty();
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
