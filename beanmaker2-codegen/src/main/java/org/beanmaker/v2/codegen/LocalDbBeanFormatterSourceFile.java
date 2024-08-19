package org.beanmaker.v2.codegen;

import static org.beanmaker.v2.codegen.BaseCode.DEFAULT_PROJECT_PARAMETERS;

public class LocalDbBeanFormatterSourceFile extends BaseEnumCode {

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
    protected void decorateJavaEnum() {
        javaEnum.implementsInterface("DbBeanFormatter");
    }

    @Override
    protected void enumerateConstants() {
        javaEnum.addEnumConstant("INSTANCE");
    }

}
