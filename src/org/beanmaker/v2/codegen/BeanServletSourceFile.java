package org.beanmaker.v2.codegen;

import org.jcodegen.java.Visibility;

public class BeanServletSourceFile extends BeanCode {

    public BeanServletSourceFile(String beanName, String packageName) {
        super(beanName, packageName, null, "Servlet");

        createSourceCode();
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC).markAsFinal().extendsClass(beanName + "ServletBase");
    }

    @Override
    protected void addCoreFunctionality() { }

}
