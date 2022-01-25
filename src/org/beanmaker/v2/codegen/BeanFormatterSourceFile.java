package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

public class BeanFormatterSourceFile extends BeanCode {

    public BeanFormatterSourceFile(String beanName, String packageName) {
        super(beanName, packageName, null, "Formatter");

        createSourceCode();
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.extendsClass(beanName  + "FormatterBase");
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
