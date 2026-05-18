package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.Visibility;

public class BeanCsvImportSourceFile extends BeanCode {

    public BeanCsvImportSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanCsvImportSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "CsvImport", projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        importsManager.addImport("org.beanmaker.v2.runtime.csv.DataFile");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC).markAsFinal().extendsClass(beanName + "CsvImportBase");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DataFile", "dataFile"))
                        .addContent(new FunctionCall("super")
                                .addArgument("dataFile")
                                .byItself()
                        )
                )
                .addContent(EMPTY_LINE)
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DataFile", "dataFile"))
                        .addArgument(new FunctionArgument("DbBeanLocalization", "dbBeanLocalization"))
                        .addContent(new FunctionCall("super")
                                .addArguments("dataFile", "dbBeanLocalization")
                                .byItself()
                        )
                )
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
