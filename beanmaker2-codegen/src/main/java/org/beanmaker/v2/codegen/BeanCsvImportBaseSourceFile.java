package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

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
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanEditor");
        importsManager.addImport("org.beanmaker.v2.runtime.csv.BeanImportBase");
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
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("BeanImportBase");
        applySealedModifier(beanName + "CsvImport");
    }

    @Override
    protected void addConstructors() {
        var superCall = new FunctionCall("super")
                .addArgument("dataFile")
                .addArgument(beanName + "Editor.class")
                .byItself();

        for (var column: columns)
            superCall.addArgument(Strings.quickQuote(column.getJavaName()));

        javaClass
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PACKAGE_PRIVATE)
                        .addArgument(new FunctionArgument("DataFile", "dataFile"))
                        .addContent(superCall))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() {
        addSetFieldsFunction();
        addFieldGetters();
    }

    private void addSetFieldsFunction() {
        var function = new FunctionDeclaration("setFields")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("DbBeanEditor", "dbBeanEditor"))
                .addArgument(new FunctionArgument("DataEntry", "dataEntry"))
                .addContent(new VarDeclaration("var", "editor", "(" + beanName + "Editor) dbBeanEditor"));

        for (var column: columns) {
            if (!column.isId()) {
                String fieldName = column.getCapitalizedJavaName();
                function.addContent(
                        new FunctionCall("set" + fieldName, "editor")
                                .addArgument(new FunctionCall("get" + fieldName).addArgument("dataEntry"))
                                .byItself()
                );
            }
        }

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private void addFieldGetters() {
        for (var column: columns) {
            if (!column.isId()) {
                javaClass.addContent(
                        new FunctionDeclaration("get" + column.getCapitalizedJavaName(), column.getJavaType())
                                .addArgument(new FunctionArgument("DataEntry", "dataEntry"))
                                .addContent(new ReturnStatement(
                                        new FunctionCall("get" + column.getCapitalizedJavaType() + "Value", "dataEntry")
                                                .addArgument(new FunctionCall("get", "getFieldToHeaderMap()")
                                                        .addArgument(Strings.quickQuote(column.getJavaName())))
                                ))
                ).addContent(EMPTY_LINE);
            }
        }
    }

}
