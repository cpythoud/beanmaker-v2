package org.beanmaker.v2.codegen;

import org.beanmaker.v2.codegen.java.Condition;
import org.beanmaker.v2.codegen.java.ExceptionThrow;
import org.beanmaker.v2.codegen.java.FunctionArgument;
import org.beanmaker.v2.codegen.java.FunctionCall;
import org.beanmaker.v2.codegen.java.FunctionDeclaration;
import org.beanmaker.v2.codegen.java.IfBlock;
import org.beanmaker.v2.codegen.java.ObjectCreation;
import org.beanmaker.v2.codegen.java.ReturnStatement;
import org.beanmaker.v2.codegen.java.TernaryOperator;
import org.beanmaker.v2.codegen.java.VarDeclaration;
import org.beanmaker.v2.codegen.java.Visibility;

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

        if (columns.isVersioned())
            importsManager.addImport("org.beanmaker.v2.runtime.VersionedBeanEditor");
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
        addGetInstanceFunctions();
        addChangeOrderFunction();
        addDisplayTableFunction();
        if (columns.isVersioned())
            addVersionedEditorFunction();
    }

    private void addGetHTMLViewFunction() {
        String editorClass = beanName + "Editor";
        String editorObject = uncapitalize(editorClass);

        javaClass
                .addContent(
                        new FunctionDeclaration("getHTMLView", "DbBeanHTMLViewInterface")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addArgument(new FunctionArgument("String", "idOrSid"))
                                .addArgument(new FunctionArgument("HttpRequestParameters", "requestParameters"))
                                .addException("ServletException")
                                .addContent(VarDeclaration.declareAndInit(editorClass, editorObject))
                                .addContent(EMPTY_LINE)
                                .addContent(new IfBlock(new Condition("!idOrSid.equals(\"0\")"))
                                        .addContent(new FunctionCall("setIdOrSid", editorObject)
                                                        .byItself()
                                                .addArgument("idOrSid")))
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
                        new FunctionDeclaration("getSubmitBeanIdOrSid", "String")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addArgument(new FunctionArgument("HttpRequestParameters", "requestParameters"))
                                .addContent(new ReturnStatement(new FunctionCall("getBeanIdOrSid")
                                        .addArguments("requestParameters", quickQuote("submitted" + beanName)))))
                .addContent(EMPTY_LINE);
    }

    private void addGetInstanceFunctions() {
        javaClass
                .addContent(getInstanceFunction()
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new ReturnStatement(new ObjectCreation(beanName + "Editor").addArgument("id"))))
                .addContent(EMPTY_LINE)
                .addContent(getInstanceFunction()
                        .addArgument(new FunctionArgument("String", "idOrSid"))
                        .addContent(new ReturnStatement(
                                new FunctionCall("fromIdOrSid", beanName + "Editor").addArgument("idOrSid"))))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getInstanceFunction() {
        return new FunctionDeclaration("getInstance", "DbBeanEditor")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED);
    }

    /*private void addGetInstanceFunction() {
        javaClass
                .addContent(
                        new FunctionDeclaration("getInstance", "DbBeanEditor")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addArgument(new FunctionArgument("String", "idOrSid"))
                                .addContent(new ReturnStatement(
                                        new FunctionCall("fromIdOrSid", beanName + "Editor").addArgument("idOrSid"))))
                .addContent(EMPTY_LINE);
    }*/

    private void addChangeOrderFunction() {
        var functionDeclaration = new FunctionDeclaration("changeOrder", "String")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("String", "idOrSid"))
                .addArgument(new FunctionArgument("ChangeOrderDirection", "direction"))
                .addArgument(new FunctionArgument("String", "companionIdOrSid"))
                .addArgument(new FunctionArgument("HttpRequestParameters", "requestParameters"));

        if (columns.hasItemOrder())
            functionDeclaration
                    .addContent(new VarDeclaration(
                            "var",
                            "editor",
                            new FunctionCall("fromIdOrSid", beanName + "Editor").addArgument("idOrSid")))
                    .addContent(new FunctionCall("setCurrentDbBeanLanguage", "editor")
                            .byItself()
                            .addArgument(new FunctionCall("getLanguage")
                                    .addArgument(new FunctionCall("getSession", "requestParameters"))))
                    .addContent(new ReturnStatement(new FunctionCall("changeOrder")
                            .addArgument("editor")
                            .addArgument("direction")
                            .addArgument(new TernaryOperator(
                                    new Condition("!companionIdOrSid.equals(\"0\")"),
                                    new FunctionCall("fromIdOrSid", beanName).addArgument("companionIdOrSid"),
                                    "null"))));

        else
            functionDeclaration.addContent(new ExceptionThrow("UnsupportedOperationException")
                    .addArgument(quickQuote(beanName + " beans have no ordering. (No itemOrder field present.)")));

        javaClass.addContent(functionDeclaration).addContent(EMPTY_LINE);
    }

    private void addDisplayTableFunction() {
        javaClass
                .addContent(
                        new FunctionDeclaration("getTableClass", "String")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addContent(
                                        new ReturnStatement(
                                                quickQuote(packageName + "." + beanName + "MasterTableView")
                                        )
                                )
        ).addContent(EMPTY_LINE);
    }

    private void addVersionedEditorFunction() {
        javaClass
                .addContent(
                        new FunctionDeclaration("getVersionedBeanEditor", "VersionedBeanEditor")
                                .annotate("@Override")
                                .visibility(Visibility.PROTECTED)
                                .addArgument(new FunctionArgument("String", "idOrSid"))
                                .addArgument(new FunctionArgument("HttpRequestParameters", "requestParameters"))
                                .addException("ServletException")
                                .addContent(
                                        new ReturnStatement(
                                                new FunctionCall("fromIdOrSid", beanName + "Editor")
                                                        .addArgument("idOrSid"))
                                )
                )
                .addContent(EMPTY_LINE);
    }

}
