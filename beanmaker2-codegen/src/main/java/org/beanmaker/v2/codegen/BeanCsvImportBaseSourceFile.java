package org.beanmaker.v2.codegen;

import org.beanmaker.v2.codegen.java.Assignment;
import org.beanmaker.v2.codegen.java.ConstructorDeclaration;
import org.beanmaker.v2.codegen.java.FunctionArgument;
import org.beanmaker.v2.codegen.java.FunctionCall;
import org.beanmaker.v2.codegen.java.FunctionDeclaration;
import org.beanmaker.v2.codegen.java.ReturnStatement;
import org.beanmaker.v2.codegen.java.VarDeclaration;
import org.beanmaker.v2.codegen.java.Visibility;

import org.beanmaker.v2.util.Strings;

import java.util.Set;

public class BeanCsvImportBaseSourceFile extends BeanCodeWithDBInfo {

    private final Set<String> types;

    public BeanCsvImportBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanCsvImportBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "CsvImportBase", columns, projectParameters);

        types = columns.getJavaTypes();

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        importsManager.addImport("org.beanmaker.v2.runtime.csv.DataEntry");
        importsManager.addImport("org.beanmaker.v2.runtime.csv.DataFile");

        if (types.contains("Date"))
            importsManager.addImport("java.sql.Date");
        if (types.contains("Time"))
            importsManager.addImport("java.sql.Time");
        if (types.contains("Timestamp"))
            importsManager.addImport("java.sql.Timestamp");
        if (types.contains("Money"))
            importsManager.addImport("org.beanmaker.v2.util.Money");
        if (types.contains("DecimalValue"))
            importsManager.addImport("org.beanmaker.v2.util.DecimalValue");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("LocalCsvImport");
        applySealedModifier(beanName + "CsvImport");
    }

    @Override
    protected void addProperties() {
        javaClass
                .addContent(new VarDeclaration("DbBeanLocalization", "dbBeanLocalization").markAsFinal())
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(startConstructor().addContent(
                        new FunctionCall("this").byItself().addArguments("dataFile", "null")
                ))
                .addContent(EMPTY_LINE);

        var superCall = new FunctionCall("super")
                .addArgument("dataFile")
                .addArgument(beanName + "Editor.class")
                .byItself();

        for (var column: columns)
            superCall.addArgument(Strings.quickQuote(column.getJavaName()));

        javaClass
                .addContent(startConstructor()
                        .addArgument(new FunctionArgument("DbBeanLocalization", "dbBeanLocalization"))
                        .addContent(superCall)
                        .addContent(new Assignment("this.dbBeanLocalization", "dbBeanLocalization"))
                )
                .addContent(EMPTY_LINE);
    }

    private ConstructorDeclaration startConstructor() {
        return javaClass.createConstructor()
                .visibility(Visibility.PACKAGE_PRIVATE)
                .addArgument(new FunctionArgument("DataFile", "dataFile"));
    }

    @Override
    protected void addCoreFunctionality() {
        addSetFieldsFunction();
        addSetters();
        addFieldGetters();
    }

    private void addSetFieldsFunction() {
        var function = new FunctionDeclaration("setFields")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("DataEntry", "dataEntry"))
                .addContent(new VarDeclaration("var", "editor", "(" + beanName + "Editor) getEditor()"));

        for (var column: columns) {
            if (!column.isId()) {
                String fieldName = column.getCapitalizedJavaName();
                function.addContent(
                        new FunctionCall("set" + fieldName)
                                .addArguments("editor", "dataEntry")
                                .byItself()
                );
            }
        }

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    void addSetters() {
        for (var column: columns) {
            if (!column.isId()) {
                String fieldName = column.getCapitalizedJavaName();
                javaClass.addContent(
                        new FunctionDeclaration("set" + fieldName)
                                .addArgument(new FunctionArgument(beanName + "Editor", "editor"))
                                .addArgument(new FunctionArgument("DataEntry", "dataEntry"))
                                .addContent(
                                        new FunctionCall("set" + fieldName, "editor")
                                                .addArgument(new FunctionCall("get" + fieldName)
                                                        .addArgument("dataEntry"))
                                                .byItself()
                                )
                ).addContent(EMPTY_LINE);
            }
        }
    }

    private void addFieldGetters() {
        for (var column: columns) {
            if (!column.isId()) {
                String type = column.getCapitalizedJavaType();
                String dataEntryFunctionName = "get" + type + (type.equals("DecimalValue") ? "" : "Value");
                var dataEntryFunction = new FunctionCall(dataEntryFunctionName)
                        .addArgument("dataEntry")
                        .addArgument(Strings.quickQuote(column.getJavaName()));
                if (type.equals("DecimalValue")) {
                    dataEntryFunction.addArgument(
                            new FunctionCall(
                                    "get" + column.getCapitalizedJavaName() + "DecimalValueParser",
                                    beanName + "Parameters.INSTANCE")
                                    .addArgument("dbBeanLocalization")
                    );
                }
                javaClass.addContent(
                        new FunctionDeclaration("get" + column.getCapitalizedJavaName(), column.getJavaType())
                                .addArgument(new FunctionArgument("DataEntry", "dataEntry"))
                                .addContent(new ReturnStatement(dataEntryFunction))
                ).addContent(EMPTY_LINE);
            }
        }
    }

}
