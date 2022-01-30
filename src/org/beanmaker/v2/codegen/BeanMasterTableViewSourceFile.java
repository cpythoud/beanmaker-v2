package org.beanmaker.v2.codegen;

import org.jcodegen.java.Visibility;

public class BeanMasterTableViewSourceFile extends BeanCode {

    public BeanMasterTableViewSourceFile(String beanName, String packageName) {
        super(beanName, packageName, null, "MasterTableView");

        createSourceCode();
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC).markAsFinal().extendsClass(beanName + "MasterTableViewBase");
    }

    @Override
    protected void addCoreFunctionality() { }

}
