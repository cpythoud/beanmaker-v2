package org.beanmaker.v2.codegen;

import org.jcodegen.java.Condition;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ReturnStatement;

import java.util.ArrayList;
import java.util.List;

import static org.beanmaker.v2.codegen.BaseCode.DEFAULT_PROJECT_PARAMETERS;
import static org.beanmaker.v2.codegen.BeanCode.chopID;
import static org.beanmaker.v2.util.Strings.capitalize;

public class BeanDataModelBaseSourceFile extends BaseInterfaceCode {

    public BeanDataModelBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanDataModelBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, "DataModelBase", columns, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        var types = columns.getJavaTypes();

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

        if (columns.hasLastUpdate())
            throw new UnsupportedOperationException("last_update field not supported in current implementation");

        if (columns.hasLabels()) {
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLabel");
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");
        }
        if (columns.hasFiles())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFile");

        importsManager.addStaticImport("org.beanmaker.v2.runtime.FieldEquality.areEqual");
    }

    @Override
    protected void addFunctionDeclarations() {
        addGetters();
        javaInterface.addContent(EMPTY_LINE);
        addEmptyCheckFunctions();
        javaInterface.addContent(EMPTY_LINE);
        addIdenticalContentComparisonFunction();
        javaInterface.addContent(EMPTY_LINE);
    }

    private void addGetters() {
        for (Column column: columns.getList())
            addGetter(column);
    }

    private void addGetter(Column column) {
        if (!column.isItemOrder()) {
            String type = column.getJavaType();
            var getter = new FunctionDeclaration(getGetterFunctionName(column), type).emptyBody();

            javaInterface.addContent(getter);
            if (column.isId())
                javaInterface.addContent(EMPTY_LINE);

            if (column.isBeanReference()) {
                if (column.isLabelReference())
                    addLabelSpecificGetterFunctions(column);
                else if (column.isFileReference())
                    addFileGetterFunction(column);
                else if (!column.isId())
                    addBeanGetterFunction(column);
            }
        }
    }

    private String getGetterFunctionName(Column column) {
        String type = column.getJavaType();
        String name = column.getJavaName();
        String getterPrefix = (type.equals("Boolean") || type.equals("boolean")) ? "is" : "get";
        return getterPrefix + capitalize(name);
    }

    private void addLabelSpecificGetterFunctions(Column column) {
        String fieldName = column.getJavaName();
        String functionName = "get" + chopID(fieldName);

        var labelFunction = new FunctionDeclaration(functionName, "DbBeanLabel").emptyBody();
        var perLanguageLabelFunction = new FunctionDeclaration(functionName, "String").emptyBody()
                .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"));

        javaInterface
                .addContent(labelFunction)
                .addContent(perLanguageLabelFunction);
    }

    private void addFileGetterFunction(Column column) {
        javaInterface.addContent(
                new FunctionDeclaration("get" + chopID(column.getJavaName()), "DbBeanFile").emptyBody()
        );
    }

    private void addBeanGetterFunction(Column column) {
        String type = column.isOriginalBeanId() ? beanName : column.getAssociatedBeanClass();
        String name = column.getJavaName();
        javaInterface.addContent(new FunctionDeclaration("get" + chopID(name), type).emptyBody());
    }

    private void addEmptyCheckFunctions() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addEmptyCheckFunction(column);
    }

    private void addEmptyCheckFunction(Column column) {
        javaInterface.addContent(getIsEmptyFunctionDeclaration(column));
    }

    private FunctionDeclaration getIsEmptyFunctionDeclaration(Column column) {
        return new FunctionDeclaration(getIsEmptyFunctionName(column), "boolean").emptyBody();
    }

    private String getIsEmptyFunctionName(Column column) {
        return "is" + capitalize(column.getJavaName()) + "Empty";
    }

    private void addIdenticalContentComparisonFunction() {
        var function = new FunctionDeclaration("isContentIdentical", "boolean")
                .markAsDefault()
                .addArgument(new FunctionArgument(beanName + "DataModel", beanVarName));

        var comparisonFunctionCalls = getComparisonFunctionCalls();
        if (comparisonFunctionCalls.isEmpty()) {
            function.addContent(new ReturnStatement("true"));
        } else if (comparisonFunctionCalls.size() == 1) {
            function.addContent(new ReturnStatement(comparisonFunctionCalls.get(0)));
        } else {
            var condition = new Condition(comparisonFunctionCalls.get(0));
            for (int i = 1; i < comparisonFunctionCalls.size(); ++i)
                condition.andCondition(new Condition(comparisonFunctionCalls.get(i)));
            function.addContent(new ReturnStatement(condition));
        }

        javaInterface.addContent(function);
    }

    private List<FunctionCall> getComparisonFunctionCalls() {
        var functionCalls = new ArrayList<FunctionCall>();
        for (Column column: columns.getList()) {
            if (!column.isSpecial()) {
                String functionName = getGetterFunctionName(column);
                functionCalls.add(
                        new FunctionCall("areEqual")
                                .addArgument(new FunctionCall(functionName))
                                .addArgument(new FunctionCall(functionName, beanVarName))
                );
            }
        }
        return functionCalls;
    }

}
