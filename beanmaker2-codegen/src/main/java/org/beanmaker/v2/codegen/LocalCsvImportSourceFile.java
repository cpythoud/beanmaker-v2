package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.Visibility;

import java.util.List;

public class LocalCsvImportSourceFile extends BaseCode {

    private static final List<String> JAVA_UTIL_IMPORTS =
            createImportList("org.beanmaker.v2.runtime.csv", "BeanImportBase", "DataFile");

    public LocalCsvImportSourceFile(String packageName) {
        this(packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public LocalCsvImportSourceFile(String packageName, ProjectParameters projectParameters) {
        super("LocalCsvImport", packageName, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(JAVA_UTIL_IMPORTS);
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("BeanImportBase");
    }

    @Override
    protected void addConstructors() {
        var superCall = new FunctionCall("super")
                .addArgument("dataFile")
                .addArgument("editorClass")
                .addArgument("fields")
                .byItself();

        javaClass
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PACKAGE_PRIVATE)
                        .addArgument(new FunctionArgument("DataFile", "dataFile"))
                        .addArgument(new FunctionArgument("Class<?>", "editorClass"))
                        .addArgument(new FunctionArgument("String...", "fields"))
                        .addContent(superCall))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
