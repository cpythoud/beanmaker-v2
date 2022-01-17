package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.Visibility;

import java.util.List;

public class HTMLViewSourceFile extends BaseCode {

    private static final List<String> RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime",
                    "BaseHTMLView", "DbBeanEditor", "DbBeanHTMLViewInterface", "DbBeanLocalization");

    public HTMLViewSourceFile(String packageName) {
        super("HTMLView", packageName);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(RUNTIME_IMPORTS);
    }

    @Override
    protected void decorateJavaClass() {
        javaClass
                .markAsAbstract()
                .extendsClass("BaseHTMLView")
                .implementsInterface("DbBeanHTMLViewInterface");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanEditor", "editor"))
                        .addArgument(new FunctionArgument("DbBeanLocalization", "dbBeanLocalization"))
                        .addContent(new FunctionCall("super").byItself().addArguments("editor", "dbBeanLocalization")))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
