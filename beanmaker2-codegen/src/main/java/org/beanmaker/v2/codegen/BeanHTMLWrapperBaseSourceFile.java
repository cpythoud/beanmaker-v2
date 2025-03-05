package org.beanmaker.v2.codegen;

import org.jcodegen.java.Assignment;
import org.jcodegen.java.Condition;
import org.jcodegen.java.ExceptionThrow;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.Visibility;

public class BeanHTMLWrapperBaseSourceFile extends BeanCodeWithDBInfo {

    private final String wrapperClass;
    private final String editorClass;
    private final String htmlFormClass;

    public BeanHTMLWrapperBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanHTMLWrapperBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "HTMLWrapperBase", columns, projectParameters);

        wrapperClass = beanName + "HTMLWrapper";
        editorClass = beanName + "Editor";
        htmlFormClass = beanName + "HTMLView";

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract();
        applySealedModifier(wrapperClass);
    }

    @Override
    protected void addProperties() {
        addProperty(beanName, "bean", false, null);
        addProperty("DbBeanLanguage", "language", false, null);
    }

    @Override
    protected void addCoreFunctionality() {
        javaClass.addContent(EMPTY_LINE);
        addSetIdFunction();
        addLanguageSetterFunction();
        addBeanGetter();
        addHtmlFormGetter();
    }

    private void addSetIdFunction() {
        javaClass.addContent(
                new FunctionDeclaration("setId")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new Assignment("bean", new ObjectCreation(beanName).addArgument("id")))
        ).addContent(EMPTY_LINE);
    }

    private void addLanguageSetterFunction() {
        javaClass.addContent(
                new FunctionDeclaration("setLanguage")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "language"))
                        .addContent(new Assignment("this.language", "language"))
        ).addContent(EMPTY_LINE);
    }

    private void addBeanGetter() {
        javaClass.addContent(
                new FunctionDeclaration("getBean", beanName)
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement("bean"))
        ).addContent(EMPTY_LINE);
    }

    private void addHtmlFormGetter() {
        javaClass.addContent(
                new FunctionDeclaration("getForm", "String")
                        .visibility(Visibility.PUBLIC)
                        .addContent(
                                new IfBlock(new Condition("bean == null"))
                                        .addContent(ExceptionThrow.getThrowExpression(
                                                "IllegalArgumentException",
                                                "No bean set"))
                        ).addContent(
                                new IfBlock(new Condition("language == null"))
                                        .addContent(ExceptionThrow.getThrowExpression(
                                                "IllegalArgumentException",
                                                "No language set"))
                        ).addContent(EMPTY_LINE).addContent(
                                new ReturnStatement(
                                        new FunctionCall(
                                                "getHtmlForm",
                                                new ObjectCreation(htmlFormClass)
                                                        .addArgument(new ObjectCreation(editorClass)
                                                                .addArgument("bean"))
                                                        .addArgument("language")))
                        )
        ).addContent(EMPTY_LINE);
    }

}
