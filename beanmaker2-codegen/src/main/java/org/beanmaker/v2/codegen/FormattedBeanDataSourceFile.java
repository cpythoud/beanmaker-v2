package org.beanmaker.v2.codegen;

import org.beanmaker.v2.codegen.java.ConstructorDeclaration;
import org.beanmaker.v2.codegen.java.FunctionArgument;
import org.beanmaker.v2.codegen.java.FunctionCall;
import org.beanmaker.v2.codegen.java.Visibility;

public class FormattedBeanDataSourceFile extends BeanCode {

    public FormattedBeanDataSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public FormattedBeanDataSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, "Formatted", "Data", projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFormatter");
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsFinal().visibility(Visibility.PUBLIC).extendsClass(className + "Base");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(createConstructor()
                        .addContent(superCall()))
                .addContent(EMPTY_LINE)
                .addContent(createConstructor()
                        .addArgument(new FunctionArgument("DbBeanFormatter", "formatter"))
                        .addContent(superCall().addArgument("formatter")))
                .addContent(EMPTY_LINE);
    }

    private ConstructorDeclaration createConstructor() {
        return javaClass.createConstructor()
                .addArgument(new FunctionArgument(beanName, beanVarName))
                .addArgument(new FunctionArgument("DbBeanLocalization", "localization"));
    }

    private FunctionCall superCall() {
        return new FunctionCall("super").byItself().addArguments(beanVarName, "localization");
    }

    @Override
    protected void addCoreFunctionality() { }

}
