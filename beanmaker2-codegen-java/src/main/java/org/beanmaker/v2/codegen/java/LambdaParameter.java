package org.beanmaker.v2.codegen.java;

public class LambdaParameter {

    private final String simpleParameter;
    private final FunctionArgument typedParameter;

    public LambdaParameter(String simpleParameter) {
        this.simpleParameter = simpleParameter;
        typedParameter = null;
    }

    public LambdaParameter(FunctionArgument typedParameter) {
        simpleParameter = null;
        this.typedParameter = typedParameter;
    }

    public boolean isSimpleParameter() {
        return typedParameter == null;
    }

    public boolean isTypedParameter() {
        return simpleParameter == null;
    }

    public String getSimpleParameter() {
        return simpleParameter;
    }

    public FunctionArgument getTypedParameter() {
        return typedParameter;
    }

    @Override
    public String toString() {
        if (isSimpleParameter())
            return simpleParameter;

        return typedParameter.toString();
    }

}
