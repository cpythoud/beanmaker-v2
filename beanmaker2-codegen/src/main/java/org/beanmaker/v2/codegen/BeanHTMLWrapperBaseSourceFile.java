package org.beanmaker.v2.codegen;

import org.beanmaker.v2.codegen.java.ChainedFunctionCalls;
import org.beanmaker.v2.codegen.java.Condition;
import org.beanmaker.v2.codegen.java.FunctionArgument;
import org.beanmaker.v2.codegen.java.FunctionCall;
import org.beanmaker.v2.codegen.java.FunctionDeclaration;
import org.beanmaker.v2.codegen.java.IfBlock;
import org.beanmaker.v2.codegen.java.ObjectCreation;
import org.beanmaker.v2.codegen.java.ReturnStatement;
import org.beanmaker.v2.codegen.java.Visibility;

import static org.beanmaker.v2.util.Strings.capitalize;

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
        if (columns.hasSidField())
            addSetSidFunctions();
        if (columns.hasUniqueCodeField())
            addSetCodeFunction();
        addBeanGetter();
        if (columns.hasSidField())
            addSidGetterFunctions();
        addHtmlViewGetter();
    }

    private void addSetIdFunction() {
        javaClass.addContent(
                new FunctionDeclaration("setId")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new FunctionCall("setBean").byItself()
                                .addArgument(new ObjectCreation(beanName).addArgument("id")))
        ).addContent(EMPTY_LINE);
    }

    private void addSetSidFunctions() {
        addSetSidFunction("sid");
        addSetSidFunction("idOrSid");
    }

    private void addSetSidFunction(String varName) {
        javaClass.addContent(
                new FunctionDeclaration("set" + capitalize(varName))
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("String", varName))
                        .addContent(new FunctionCall("setBean").byItself()
                                .addArgument(new FunctionCall("fromIdOrSid", beanName).addArgument(varName)))
        ).addContent(EMPTY_LINE);
    }

    private void addSetCodeFunction() {
        javaClass.addContent(
                new FunctionDeclaration("setCode")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("String", "code"))
                        .addContent(new FunctionCall("setBean").byItself()
                                .addArgument(new FunctionCall(
                                        "orElseThrow",
                                        new FunctionCall("getFromCode", beanName).addArgument("code"))))
        ).addContent(EMPTY_LINE);
    }

    private void addBeanGetter() {
        javaClass.addContent(
                new FunctionDeclaration("getBean", beanName)
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement("(%s) super.getBean()".formatted(beanName)))
        ).addContent(EMPTY_LINE);
    }

    private void addSidGetterFunctions() {
        javaClass.addContent(
                new FunctionDeclaration("getSid", "String")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new ChainedFunctionCalls("getBean").chain("getSid")))
        ).addContent(EMPTY_LINE);

        javaClass.addContent(
                new FunctionDeclaration("getIdOrSid", "String")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("getIdOrSid", "super")
                                .addArgument(beanName + "Parameters.INSTANCE")))
        ).addContent(EMPTY_LINE);
    }

    private void addHtmlViewGetter() {
        javaClass.addContent(
                new FunctionDeclaration("getHtmlView", htmlFormClass)
                        .annotate("@Override")
                        .visibility(Visibility.PROTECTED)
                        .addContent(new FunctionCall("checkLanguage").byItself())
                        .addContent(new IfBlock(new Condition(new FunctionCall("noBeanYet")))
                                .addContent(
                                        new ReturnStatement(
                                                new ObjectCreation(htmlFormClass)
                                                        .addArgument(new ObjectCreation(editorClass))
                                                        .addArgument(new FunctionCall("getLanguage")))
                                ))
                        .addContent(EMPTY_LINE)
                        .addContent(
                                new ReturnStatement(
                                        new ObjectCreation(htmlFormClass)
                                                .addArgument(new ObjectCreation(editorClass)
                                                        .addArgument(new FunctionCall("getBean")))
                                                .addArgument(new FunctionCall("getLanguage")))
                        )
        ).addContent(EMPTY_LINE);
    }

}
