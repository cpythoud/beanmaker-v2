package org.beanmaker.v2.codegen;

import static org.beanmaker.v2.codegen.BaseCode.DEFAULT_PROJECT_PARAMETERS;

public class BeanParametersSourceFile extends BaseEnumCode {

    public BeanParametersSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanParametersSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName + "Parameters", packageName, projectParameters);

        createSourceCode();
    }

    @Override
    protected void decorateJavaEnum() {
        javaEnum.implementsInterface(enumName + "Base");
    }

    @Override
    protected void enumerateConstants() {
        javaEnum.addEnumConstant("INSTANCE");
    }

}
