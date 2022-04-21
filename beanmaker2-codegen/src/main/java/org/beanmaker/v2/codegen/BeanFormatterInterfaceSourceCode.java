package org.beanmaker.v2.codegen;

public class BeanFormatterInterfaceSourceCode extends BaseInterfaceCode {

    public BeanFormatterInterfaceSourceCode(String beanName, String packageName, Columns columns) {
        super(beanName, packageName, "FormatterInterface", columns);

        createSourceCode();
    }

    @Override
    protected void decorateJavaInterface() {
        javaInterface.extendsInterface(beanName + "FormatterInterfaceBase");
    }

    @Override
    protected void addFunctionDeclarations() { }

}
