package org.beanmaker.v2.codegen;

import org.jcodegen.java.ConstructorDeclaration;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;

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
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsFinal().extendsClass(className + "Base");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(createConstructor()
                        .addContent(superCall()))
                .addContent(EMPTY_LINE)
                .addContent(createConstructor()
                        .addArgument(new FunctionArgument(beanName + "FormatterInterface", "formatter"))
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
