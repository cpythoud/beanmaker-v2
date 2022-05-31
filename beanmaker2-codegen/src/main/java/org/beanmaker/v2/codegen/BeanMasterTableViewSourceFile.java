package org.beanmaker.v2.codegen;

import org.jcodegen.java.Visibility;

public class BeanMasterTableViewSourceFile extends BeanCode {

    public BeanMasterTableViewSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanMasterTableViewSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "MasterTableView", projectParameters);

        createSourceCode();
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC).markAsFinal().extendsClass(beanName + "MasterTableViewBase");
    }

    @Override
    protected void addCoreFunctionality() { }

}
