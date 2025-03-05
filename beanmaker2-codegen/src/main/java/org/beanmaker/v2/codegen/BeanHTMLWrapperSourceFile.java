package org.beanmaker.v2.codegen;

import org.jcodegen.java.Visibility;

public class BeanHTMLWrapperSourceFile extends BeanCode {

    public BeanHTMLWrapperSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanHTMLWrapperSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "HTMLWrapper", projectParameters);
        createSourceCode();
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC).markAsFinal().extendsClass(beanName + "HTMLWrapperBase");
    }

    @Override
    protected void addCoreFunctionality() { }

}
