package org.beanmaker.v2.codegen;

import org.jcodegen.java.Assignment;
import org.jcodegen.java.Comparison;
import org.jcodegen.java.Condition;
import org.jcodegen.java.ConstructorDeclaration;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.VarDeclaration;

import static org.beanmaker.v2.util.Strings.capitalize;
import static org.beanmaker.v2.util.Strings.quickQuote;

public class FormattedBeanDataBaseSourceFile extends BeanCodeWithDBInfo {

    public FormattedBeanDataBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public FormattedBeanDataBaseSourceFile(
            String beanName,
            String packageName,
            Columns columns,
            ProjectParameters projectParameters)
    {
        super(beanName, packageName, "Formatted", "DataBase", columns, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFormatter");
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");

        if (columns.hasLabels())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract();
        applySealedModifier("Formatted" + beanName + "Data");
    }

    @Override
    protected void addProperties() {
        javaClass
                .addContent(new VarDeclaration(beanName, beanVarName).markAsFinal())
                .addContent(new VarDeclaration("DbBeanLocalization", "localization").markAsFinal())
                .addContent(new VarDeclaration("DbBeanFormatter", "formatter").markAsFinal())
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(createConstructor()
                        .addContent(new FunctionCall("this")
                                .byItself()
                                .addArguments(beanVarName, "localization", "LocalDbBeanFormatter.INSTANCE")))
                .addContent(EMPTY_LINE)
                .addContent(createConstructor()
                        .addArgument(new FunctionArgument("DbBeanFormatter", "formatter"))
                        .addContent(new Assignment("this." + beanVarName, beanVarName))
                        .addContent(new Assignment("this.localization", "localization"))
                        .addContent(new Assignment("this.formatter", "formatter")))
                .addContent(EMPTY_LINE);
    }

    private ConstructorDeclaration createConstructor() {
        return javaClass.createConstructor()
                .addArgument(new FunctionArgument(beanName, beanVarName))
                .addArgument(new FunctionArgument("DbBeanLocalization", "localization"));
    }

    @Override
    protected void addCoreFunctionality() {
        addBeanGetter();
        addFormattedDataGetters();
        addLabelGetters();
    }

    private void addBeanGetter() {
        javaClass
                .addContent(new FunctionDeclaration("get" + beanName, beanName)
                        .addContent(new ReturnStatement(beanVarName)))
                .addContent(EMPTY_LINE);
    }

    private void addFormattedDataGetters() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addFormattedDataGetter(column);
    }

    private void addFormattedDataGetter(Column column) {
        String capName = column.isBeanReference() ? chopID(column.getJavaName()) : capitalize(column.getJavaName());
        String functionName = (column.getJavaType().equals("Boolean") ? "is" : "get") + capName;

        var function = new FunctionDeclaration(functionName, "String");
        if (column.isBeanReference())
            function.addContent(getIdZeroTest(column)).addContent(EMPTY_LINE);
        function.addContent(new ReturnStatement(getFormattingFunctionCall(column, functionName)));
        javaClass.addContent(function).addContent(EMPTY_LINE);

        if (column.isLabelReference())
            javaClass.addContent(getLabelWithLangArgFunction(column, functionName)).addContent(EMPTY_LINE);

        if (column.isFileReference())
            javaClass.addContent(getFileLinkFunction(column, functionName)).addContent(EMPTY_LINE);

        /*javaClass
                .addContent(new FunctionDeclaration(functionName, "String")
                        .addContent(new ReturnStatement(
                                new FunctionCall("getFormatted" + capName, "formatter")
                                        .addArguments(beanVarName, "localization"))))
                .addContent(EMPTY_LINE);

        if (column.isLabelReference())
            javaClass
                    .addContent(new FunctionDeclaration(functionName, "String")
                            .addArgument(new FunctionArgument("DbBeanLanguage", "language"))
                            .addContent(new ReturnStatement(
                                    new FunctionCall("getFormatted" + capName, "formatter")
                                            .addArguments(beanVarName, "language"))))
                    .addContent(EMPTY_LINE);
        else if (column.isFileReference())
            javaClass
                    .addContent(new FunctionDeclaration(functionName + "Link", "String")
                            .addContent(new ReturnStatement(
                                    new FunctionCall("get" + capName + "Link", "formatter")
                                            .addArguments(beanVarName, "localization"))))
                    .addContent(EMPTY_LINE);*/
    }

    private IfBlock getIdZeroTest(Column column) {
        return new IfBlock(new Condition(new Comparison(
                new FunctionCall("get" + capitalize(column.getJavaName()), beanVarName), "0") ))
                .addContent(new ReturnStatement(new FunctionCall("noData", "formatter")));
    }

    private FunctionCall getFormattingFunctionCall(Column column, String dataRetrievalFunction) {
        String functionName;
        if (column.isFileReference())
            functionName = "formatFile";
        else if (column.isLabelReference())
            functionName = "formatLabel";
        else if (column.isBeanReference())
            functionName = "formatBean";
        else
            functionName = "format" + column.getJavaType();

        /*String capName;
        if (column.isBeanReference() || column.isFileReference() || column.isLabelReference())
            capName = chopID(column.getJavaName());
        else
            capName = capitalize(column.getJavaName());*/

        return new FunctionCall(functionName, "formatter")
                .addArgument(new FunctionCall(dataRetrievalFunction, beanVarName))
                .addArgument("localization");
    }

    private FunctionDeclaration getLabelWithLangArgFunction(Column column, String functionName) {
        return new FunctionDeclaration(functionName, "String")
                .addArgument(new FunctionArgument("DbBeanLanguage", "language"))
                .addContent(getIdZeroTest(column))
                .addContent(EMPTY_LINE)
                .addContent(new ReturnStatement(
                        new FunctionCall("formatLabel", "formatter")
                                .addArgument(
                                        new FunctionCall("get" + chopID(column.getJavaName()), beanVarName)
                                )
                                .addArgument("language")
                ));
    }

    private FunctionDeclaration getFileLinkFunction(Column column, String functionNameStart) {
        return new FunctionDeclaration(functionNameStart + "Link", "String")
                .addContent(getIdZeroTest(column))
                .addContent(EMPTY_LINE)
                .addContent(new ReturnStatement(
                        new FunctionCall("formatFileLink", "formatter")
                                .addArgument(
                                        new FunctionCall("get" + chopID(column.getJavaName()), beanVarName)
                                )
                                .addArgument("localization")
                ));
    }

    private void addLabelGetters() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addLabelGetter(column);
    }

    private void addLabelGetter(Column column) {
        String name = column.getJavaName();

        javaClass
                .addContent(new FunctionDeclaration("get" + capitalize(name) + "Label", "String")
                        .addContent(new ReturnStatement(new FunctionCall("getLabel", "localization")
                                .addArgument(quickQuote(name)))))
                .addContent(EMPTY_LINE);
    }

}
