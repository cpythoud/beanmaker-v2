package org.beanmaker.v2.codegen;

import org.jcodegen.java.Visibility;

import static org.beanmaker.v2.codegen.BaseCode.DEFAULT_PROJECT_PARAMETERS;

public class BeanDataModelSourceFile extends BaseInterfaceCode {

    public BeanDataModelSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanDataModelSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, "DataModel", null, projectParameters);

        createSourceCode();
    }

    @Override
    protected void decorateJavaInterface() {
        javaInterface.visibility(Visibility.PUBLIC).extendsInterface(beanName + "DataModelBase");
    }

    @Override
    protected void addFunctionDeclarations() { }

}
