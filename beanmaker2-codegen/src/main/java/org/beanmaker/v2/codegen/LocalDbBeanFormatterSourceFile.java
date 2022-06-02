package org.beanmaker.v2.codegen;

public class LocalDbBeanFormatterSourceFile extends BaseCode {

    public LocalDbBeanFormatterSourceFile(String packageName) {
        this(packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public LocalDbBeanFormatterSourceFile(String packageName, ProjectParameters projectParameters) {
        super("LocalDbBeanFormatter", packageName, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFormatter");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.extendsClass("DbBeanFormatter");
    }

    @Override
    protected void addCoreFunctionality() { }

}
