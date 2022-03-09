package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;

import static org.beanmaker.v2.codegen.BeanCode.chopID;

import static org.beanmaker.v2.util.Strings.capitalize;

public class BeanFormatterInterfaceSourceCode extends BaseInterfaceCode {

    public BeanFormatterInterfaceSourceCode(String beanName, String packageName, Columns columns) {
        super(beanName, packageName, "FormatterInterface", columns);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");

        if (columns.hasLabels())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");
    }

    @Override
    protected void addFunctionDeclarations() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addFunctionDeclaration(column);
    }

    private void addFunctionDeclaration(Column column) {
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
        javaInterface
                .addContent(getDefaultFormattingFunctionDeclaration(column))
                .addContent(getFormattingFunctionDeclarationWithLangugae(column))
                .addContent(EMPTY_LINE);
    }

    private FunctionCall getLabelFunction(Column column) {
        return new FunctionCall("get" + chopID(column.getJavaName()), beanVarName);
    }

    private void addFileFormattingFunctions(Column column) {
        String capName = chopID(column.getJavaName());
        var functionCall = new FunctionCall("get" + capName, beanVarName);

        javaInterface
                .addContent(getDefaultFormattingFunctionDeclaration(column))
                .addContent(new FunctionDeclaration("get" + capName + "Link", "String")
                        .emptyBody()
                        .addArgument(new FunctionArgument(beanName, beanVarName))
                        .addArgument(new FunctionArgument("DbBeanLocalization", "localization")))
                .addContent(EMPTY_LINE);
    }

    private void addBeanFormattingFunction(Column column) {
        javaInterface
                .addContent(getDefaultFormattingFunctionDeclaration(column))
                .addContent(EMPTY_LINE);
    }

    private void addStringFormattingFunction(Column column) {
        javaInterface
                .addContent(getDefaultFormattingFunctionDeclaration(column))
                .addContent(EMPTY_LINE);
    }

    private void addTypeBasedFormattingFunction(Column column) {
        javaInterface
                .addContent(getDefaultFormattingFunctionDeclaration(column))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getDefaultFormattingFunctionDeclaration(Column column) {
        return getFormattingFunctionDeclaration(column)
                .addArgument(new FunctionArgument("DbBeanLocalization", "localization"));
    }

    private FunctionDeclaration getFormattingFunctionDeclarationWithLangugae(Column column) {
        return getFormattingFunctionDeclaration(column)
                .addArgument(new FunctionArgument("DbBeanLanguage", "language"));
    }

    private FunctionDeclaration getFormattingFunctionDeclaration(Column column) {
        String fieldName = column.getJavaName();
        String functionName = "getFormatted" + (column.isBeanReference() ? chopID(fieldName) : capitalize(fieldName));

        return new FunctionDeclaration(functionName, "String")
                .emptyBody()
                .addArgument(new FunctionArgument(beanName, beanVarName));
    }

}
