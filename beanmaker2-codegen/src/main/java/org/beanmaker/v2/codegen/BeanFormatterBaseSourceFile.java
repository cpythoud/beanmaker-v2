package org.beanmaker.v2.codegen;

import org.jcodegen.java.Comparison;
import org.jcodegen.java.Condition;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.Visibility;

import static org.beanmaker.v2.util.Strings.capitalize;

public class BeanFormatterBaseSourceFile extends BeanCodeWithDBInfo {

    public BeanFormatterBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanFormatterBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "FormatterBase", columns, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");

        if (columns.hasLabels())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");

        if (columns.hasFiles())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFile");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass
                .markAsAbstract()
                .extendsClass("LocalDbBeanFormatter")
                .implementsInterface(beanName + "FormatterInterface");

        applySealedModifier(beanName + "Formatter");
    }

    @Override
    protected void addCoreFunctionality() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addFormattingFunction(column);
    }

    private void addFormattingFunction(Column column) {
        if (column.isLabelReference())
            addLabelFormattingFunctions(column);
        else if (column.isFileReference())
            addFileFormattingFunctions(column);
        else if (column.isBeanReference())
            addBeanFormattingFunction(column);
        else if (column.getJavaType().equals("String"))
            addStringFormattingFunction(column);
        else
            addTypeBasedFormattingFunction(column);
    }

    private void addLabelFormattingFunctions(Column column) {
        javaClass
                .addContent(getFormattingFunctionDeclarationWithLocalization(column)
                        .addContent(new ReturnStatement(new FunctionCall(getFormattingFunctionName(column))
                                .addArgument(beanVarName)
                                .addArgument(new FunctionCall("getLanguage", "localization")))))
                .addContent(EMPTY_LINE)
                .addContent(getFormattingFunctionDeclarationWithLanguage(column)
                        .addContent(getNoDataTestForReferencedBeanInFormatter(column))
                        .addContent(EMPTY_LINE)
                        .addContent(new ReturnStatement(getChainedLabelTestAndCall(column))))
                .addContent(EMPTY_LINE);
    }

    private String getChainedLabelTestAndCall(Column column) {
        String labelName = chopID(column.getJavaName());
        return "%s.get%s().hasDataFor(language) ? %s.get%s(language) : \"\""
                .formatted(beanVarName, labelName, beanVarName, labelName);
    }

    private IfBlock getNoDataTestForReferencedBeanInFormatter(Column column) {
        return new IfBlock(new Condition(
                new Comparison(new FunctionCall("get" + capitalize(column.getJavaName()), beanVarName), "0")))
                .addContent(new ReturnStatement(EMPTY_STRING));
    }

    private FunctionCall getLabelFunction(Column column) {
        return new FunctionCall("get" + chopID(column.getJavaName()), beanVarName);
    }

    private void addFileFormattingFunctions(Column column) {
        String capName = chopID(column.getJavaName());
        var functionCall = new FunctionCall("get" + capName, beanVarName);

        javaClass
                .addContent(getFormattingFunctionDeclarationWithLocalization(column)
                        .addContent(getNoDataTestForReferencedBeanInFormatter(column))
                        .addContent(EMPTY_LINE)
                        .addContent(new ReturnStatement(new FunctionCall("getFilename", functionCall))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("get" + capName + "Link", "String")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument(beanName, beanVarName))
                        .addArgument(new FunctionArgument("DbBeanLocalization", "localization"))
                        .addContent(getNoDataTestForReferencedBeanInFormatter(column))
                        .addContent(EMPTY_LINE)
                        .addContent(new ReturnStatement(new FunctionCall(
                                "toString",
                                new FunctionCall("getLink", "DbBeanFile").addArgument(functionCall)))))
                .addContent(EMPTY_LINE);
    }

    private void addBeanFormattingFunction(Column column) {
        var functionCall = new FunctionCall("get" + chopID(column.getJavaName()), beanVarName);

        javaClass
                .addContent(getFormattingFunctionDeclarationWithLocalization(column)
                        .addContent(getNoDataTestForReferencedBeanInFormatter(column))
                        .addContent(EMPTY_LINE)
                        .addContent(new ReturnStatement(
                                new FunctionCall("getNameForIdNamePairsAndTitles", functionCall)
                                        .addArgument(new FunctionCall("getLanguage", "localization")))))
                .addContent(EMPTY_LINE);
    }

    private void addStringFormattingFunction(Column column) {
        javaClass
                .addContent(getFormattingFunctionDeclarationWithLocalization(column)
                        .addContent(new ReturnStatement(
                                new FunctionCall("formatString")
                                        .addArgument(new FunctionCall("get" + capitalize(column.getJavaName()), beanVarName)))))
                .addContent(EMPTY_LINE);
    }

    private void addTypeBasedFormattingFunction(Column column) {
        String type = column.getJavaType();
        if (type.equals("Integer"))
            type = "Int";
        String functionName = (type.equals("Boolean") ? "is" : "get") + capitalize(column.getJavaName());

        javaClass
                .addContent(getFormattingFunctionDeclarationWithLocalization(column)
                        .addContent(new ReturnStatement(new FunctionCall("format" + type)
                                .addArgument(new FunctionCall(functionName, beanVarName))
                                .addArgument("localization"))))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getFormattingFunctionDeclarationWithLocalization(Column column) {
        return getFormattingFunctionDeclaration(column)
                .addArgument(new FunctionArgument("DbBeanLocalization", "localization"));
    }

    private FunctionDeclaration getFormattingFunctionDeclarationWithLanguage(Column column) {
        return getFormattingFunctionDeclaration(column)
                .addArgument(new FunctionArgument("DbBeanLanguage", "language"));
    }

    private FunctionDeclaration getFormattingFunctionDeclaration(Column column) {
        return new FunctionDeclaration(getFormattingFunctionName(column), "String")
                .annotate("@Override")
                .visibility(Visibility.PUBLIC)
                .addArgument(new FunctionArgument(beanName, beanVarName));
    }

    private String getFormattingFunctionName(Column column) {
        String fieldName = column.getJavaName();
        return "getFormatted" + (column.isBeanReference() ? chopID(fieldName) : capitalize(fieldName));
    }

}
