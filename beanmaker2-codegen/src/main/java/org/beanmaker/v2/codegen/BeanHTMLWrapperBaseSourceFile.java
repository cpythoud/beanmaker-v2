package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
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
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanHTMLWrapperBase");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("DbBeanHTMLWrapperBase");
        applySealedModifier(wrapperClass);
    }

    @Override
    protected void addCoreFunctionality() {
        addSetIdFunction();
        addBeanGetter();
        addHtmlFormGetter();
    }

    private void addSetIdFunction() {
        javaClass.addContent(
                new FunctionDeclaration("setId")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new FunctionCall("setBean").byItself()
                                .addArgument(new ObjectCreation(beanName).addArgument("id")))
        ).addContent(EMPTY_LINE);
    }

    private void addBeanGetter() {
        javaClass.addContent(
                new FunctionDeclaration("getBean", beanName)
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement("(%s) super.getBean()".formatted(beanName)))
        ).addContent(EMPTY_LINE);
    }

    private void addHtmlFormGetter() {
        javaClass.addContent(
                new FunctionDeclaration("getForm", "String")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new FunctionCall("checkParameters").byItself())
                        .addContent(
                                new ReturnStatement(
                                        new FunctionCall(
                                                "getHtmlForm",
                                                new ObjectCreation(htmlFormClass)
                                                        .addArgument(new ObjectCreation(editorClass)
                                                                .addArgument(new FunctionCall("getBean")))
                                                        .addArgument(new FunctionCall("getLanguage"))))
                        )
        ).addContent(EMPTY_LINE);
    }

}
