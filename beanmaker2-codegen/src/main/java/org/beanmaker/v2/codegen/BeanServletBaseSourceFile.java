package org.beanmaker.v2.codegen;

import org.jcodegen.java.Condition;
import org.jcodegen.java.ExceptionThrow;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.TernaryOperator;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;

import static org.beanmaker.v2.util.Strings.quickQuote;
import static org.beanmaker.v2.util.Strings.uncapitalize;

public class BeanServletBaseSourceFile extends BeanCodeWithDBInfo {

    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "ChangeOrderDirection", "DbBeanEditor",
                    "DbBeanHTMLViewInterface", "HttpRequestParameters");

    public BeanServletBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanServletBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "ServletBase", columns, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(BM_RUNTIME_IMPORTS);

        importsManager.addImport("javax.servlet.ServletException");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("BaseServlet");
        applySealedModifier(beanName + "Servlet");
    }

    @Override
    protected void addCoreFunctionality() {
        addGetHTMLViewFunction();
        addGetSubmitBeanIdFunction();
        addGetInstanceFunction();
        addChangeOrderFunction();
    }

    private void addGetHTMLViewFunction() {
        String editorClass = beanName + "Editor";
        String editorObject = uncapitalize(editorClass);

        javaClass
                .addContent(
                        new FunctionDeclaration("getHTMLView", "DbBeanHTMLViewInterface")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addArgument(new FunctionArgument("long", "id"))
                                .addArgument(new FunctionArgument("HttpRequestParameters", "requestParameters"))
                                .addException("ServletException")
                                .addContent(VarDeclaration.declareAndInit(editorClass, editorObject))
                                .addContent(EMPTY_LINE)
                                .addContent(new IfBlock(new Condition("id > 0"))
                                        .addContent(new FunctionCall("setId", editorObject)
                                                        .byItself()
                                                .addArgument("id")))
                                .addContent(EMPTY_LINE)
                                .addContent(new ReturnStatement(
                                        new ObjectCreation(beanName + "HTMLView")
                                                .addArgument(editorObject)
                                                .addArgument(new FunctionCall("getLanguage")
                                                        .addArgument(new FunctionCall("getSession", "requestParameters"))))))
                .addContent(EMPTY_LINE);
    }

    private void addGetSubmitBeanIdFunction() {
        javaClass
                .addContent(
                        new FunctionDeclaration("getSubmitBeanId", "long")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addArgument(new FunctionArgument("HttpRequestParameters", "requestParameters"))
                                .addContent(new ReturnStatement(new FunctionCall("getBeanId")
                                        .addArguments("requestParameters", quickQuote("submitted" + beanName)))))
                .addContent(EMPTY_LINE);
    }

    private void addGetInstanceFunction() {
        javaClass
                .addContent(
                        new FunctionDeclaration("getInstance", "DbBeanEditor")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addArgument(new FunctionArgument("long", "id"))
                                .addContent(new ReturnStatement(new ObjectCreation(beanName + "Editor")
                                        .addArgument("id"))))
                .addContent(EMPTY_LINE);
    }

    private void addChangeOrderFunction() {
        var functionDeclaration = new FunctionDeclaration("changeOrder", "String")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("long", "id"))
                .addArgument(new FunctionArgument("ChangeOrderDirection", "direction"))
                .addArgument(new FunctionArgument("long", "companionId"))
                .addArgument(new FunctionArgument("HttpRequestParameters", "requestParameters"));

        if (columns.hasItemOrder())
            functionDeclaration
                    .addContent(new VarDeclaration(
                            "var",
                            "editor",
                            new ObjectCreation(beanName + "Editor").addArgument("id")))
                    .addContent(new FunctionCall("setCurrentDbBeanLanguage", "editor")
                            .byItself()
                            .addArgument(new FunctionCall("getLanguage")
                                    .addArgument(new FunctionCall("getSession", "requestParameters"))))
                    .addContent(new ReturnStatement(new FunctionCall("changeOrder")
                            .addArgument("editor")
                            .addArgument("direction")
                            .addArgument(new TernaryOperator(
                                    new Condition("companionId > 0"),
                                    new ObjectCreation(beanName).addArgument("companionId"),
                                    "null"))));

        else
            functionDeclaration.addContent(new ExceptionThrow("UnsupportedOperationException")
                    .addArgument(quickQuote(beanName + " beans have no ordering. (No itemOrder field present.)")));

        javaClass.addContent(functionDeclaration).addContent(EMPTY_LINE);
    }

}
