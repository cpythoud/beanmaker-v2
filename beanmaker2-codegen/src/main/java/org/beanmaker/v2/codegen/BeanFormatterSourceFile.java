package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

public class BeanFormatterSourceFile extends BeanCode {

    public BeanFormatterSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanFormatterSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "Formatter", projectParameters);

        createSourceCode();
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsFinal().extendsClass(beanName  + "FormatterBase");
    }

    @Override
    protected void addStaticProperties() {
        javaClass
                .addContent(VarDeclaration.declareAndInit(className, "INSTANCE").markAsStatic().markAsFinal())
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PRIVATE)
                        .addContent(new FunctionCall("super").byItself()))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
