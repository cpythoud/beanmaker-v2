package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;

public class LocalMasterTableViewSourceFile extends BaseCode {

    public LocalMasterTableViewSourceFile(String packageName) {
        super("LocalMasterTableView", packageName);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.BaseMasterTableView");
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("BaseMasterTableView");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument("String", "tableId"))
                        .addArgument(new FunctionArgument("DbBeanLocalization", "localization"))
                        .addContent(new FunctionCall("super").byItself().addArguments("tableId", "localization")))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
