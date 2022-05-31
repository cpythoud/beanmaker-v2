package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.Visibility;

public class BeanSourceFile extends BeanCode {

    public BeanSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, null, null, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("java.sql.ResultSet");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC).markAsFinal().extendsClass(beanName + "Base");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new FunctionCall("super").byItself().addArgument("id")))
                .addContent(EMPTY_LINE)
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument("ResultSet", "rs"))
                        .addContent(new FunctionCall("super").byItself().addArgument("rs")))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
