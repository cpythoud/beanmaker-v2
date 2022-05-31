package org.beanmaker.v2.codegen;

import org.jcodegen.java.ExceptionThrow;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;

import static org.beanmaker.v2.util.Strings.quickQuote;

public class BaseServletSourceFile extends BaseCode {

    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DbBeanLanguage", "MissingImplementationException", "OperationsBaseServlet");

    public BaseServletSourceFile(String packageName) {
        this(packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BaseServletSourceFile(String packageName, ProjectParameters projectParameters) {
        super("BaseServlet", packageName, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(BM_RUNTIME_IMPORTS);

        importsManager.addImport("javax.servlet.http.HttpSession");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("OperationsBaseServlet");
    }

    @Override
    protected void addCoreFunctionality() {
        javaClass
                .addContent(new FunctionDeclaration("getLanguage", "DbBeanLanguage")
                        .annotate("@Override")
                        .visibility(Visibility.PROTECTED)
                        .addArgument(new FunctionArgument("HttpSession", "session"))
                        .addContent(new ExceptionThrow("MissingImplementationException")
                                .addArgument(quickQuote("BaseServlet.getLanguage(HttpSession)"))))
                .addContent(EMPTY_LINE);
    }

}
