package org.beanmaker.v2.codegen;

import org.jcodegen.java.Condition;
import org.jcodegen.java.ElseBlock;
import org.jcodegen.java.ForEach;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.OperatorExpression;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;

import static org.beanmaker.v2.util.Strings.capitalize;
import static org.beanmaker.v2.util.Strings.quickQuote;

public class BeanMasterTableViewBaseSourceFile extends BeanCodeWithDBInfo {

    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DbBeanInterface", "MasterTableCellDefinition");
    private static final List<String> HTML_IMPORTS =
            createImportList("org.jcodegen.html", "TdTag", "ThTag", "TrTag");

    public BeanMasterTableViewBaseSourceFile(String beanName, String packageName, Columns columns) {
        super(beanName, packageName, null, "MasterTableViewBase", columns);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(BM_RUNTIME_IMPORTS, HTML_IMPORTS);

        importsManager.addImport("java.util.List");
        importsManager.addImport("java.util.stream.Collectors");

        if (columns.hasLabelField())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("MasterTableView");
    }

    @Override
    protected void addConstructors() {
        String parametersInstance = beanName + "Parameters.INSTANCE";
        javaClass
                .addContent(javaClass.createConstructor()
                        .addContent(new FunctionCall("super")
                                .byItself()
                                .addArgument(new FunctionCall("getDatabaseTableName", parametersInstance))
                                .addArgument(new FunctionCall("getLocalization", parametersInstance))))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() {
        addFilterRowFunction();
        addFilterCellFunctions();
        addTitleRowFunction();
        addTitleCellFunctions();
        addDataFunctions();
        addDataLineFunction();
        addDataCellFunctions();
    }

    private void addFilterRowFunction() {
        var function = getFilterFunctionStart();

        for (Column column: columns.getList())
            if (!column.isSpecial()) {
                if (column.isLabelReference())
                    function.addContent(getLabelFilterCellFunctionCall(column));
                else
                    function.addContent(getFilterCellFunctionCall(column));
            }

        function
                .addContent(EMPTY_LINE)
                .addContent(getDataToggleTest())
                .addContent(EMPTY_LINE)
                .addContent(new ReturnStatement("filterRow"));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getFilterFunctionStart() {
        return new FunctionDeclaration("getFilterRow", "TrTag")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addContent(new VarDeclaration("TrTag", "filterRow", new FunctionCall("getDefaultStartOfFilterRow")))
                .addContent(EMPTY_LINE)
                .addContent(new IfBlock(new Condition("displayId"))
                        .addContent(new FunctionCall("child", "filterRow")
                                .byItself()
                                .addArgument(new FunctionCall("getIdFilterCell"))));
    }

    private FunctionCall getLabelFilterCellFunctionCall(Column column) {
        return new FunctionCall("add" + chopID(column.getJavaName()) + "FilterCellsTo")
                .byItself()
                .addArgument("filterRow");
    }

    private FunctionCall getFilterCellFunctionCall(Column column) {
        return new FunctionCall("child", "filterRow")
                .byItself()
                .addArgument(new FunctionCall("get" + capitalize(column.getJavaName()) + "FilterCell"));
    }

    private IfBlock getDataToggleTest() {
        return new IfBlock(new Condition("doDataToggle"))
                .addContent(new FunctionCall("child", "filterRow")
                        .byItself()
                        .addArgument(new FunctionCall("showMoreLessCell")));
    }

    private void addFilterCellFunctions() {
        for (Column column: columns.getList())
            if (!column.isItemOrder()) {
                if (column.isLabelReference())
                    addLabelFilterFunctions(column);
                else
                    addFilterCellFunction(column);
            }
    }

    private void addLabelFilterFunctions(Column column) {
        String name = chopID(column.getJavaName());

        javaClass
                .addContent(new FunctionDeclaration("add" + name + "FilterCellsTo")
                        .addArgument(new FunctionArgument("TrTag", "filterRow"))
                        .addContent(new IfBlock(new Condition("displayAllLanguages"))
                                .addContent(new ForEach(
                                        "DbBeanLanguage",
                                        "dbBeanLanguage",
                                        new FunctionCall("getAllActiveLanguages", "LabelManager"))
                                        .addContent(new FunctionCall("child", "filterRow")
                                                .byItself()
                                                .addArgument(new FunctionCall("get" + name + "FilterCell").addArgument("dbBeanLanguage"))))
                                .elseClause(new ElseBlock()
                                        .addContent(new FunctionCall("child", "filterRow")
                                                .byItself()
                                                .addArgument(new FunctionCall("get" + name + "FilterCell")
                                                        .addArgument(new FunctionCall("getLanguage", "dbBeanLocalization")))))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("get" + name + "FilterCell", "ThTag")
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new ReturnStatement(new FunctionCall("getStringFilterCell")
                                .addArgument(new OperatorExpression(
                                        new FunctionCall("getIso", "dbBeanLanguage"),
                                        quickQuote(name),
                                        OperatorExpression.Operator.ADD)))))
                .addContent(EMPTY_LINE);
    }

    private void addFilterCellFunction(Column column) {
        String name = column.getJavaName();
        String function = column.getJavaType().equals("Boolean") ? "getBooleanFilterCell" : "getStringFilterCell";

        javaClass
                .addContent(new FunctionDeclaration("get" + capitalize(name) + "FilterCell", "ThTag")
                        .addContent(new ReturnStatement(new FunctionCall(function).addArgument(quickQuote(name)))))
                .addContent(EMPTY_LINE);
    }

    private void addTitleRowFunction() {
        var function = getTitleFunctionStart();

        for (Column column: columns.getList())
            if (!column.isSpecial()) {
                if (column.isLabelReference())
                    function.addContent(getLabelTitleCellFunctionCall(column));
                else
                    function.addContent(getTitleCellFunctionCall(column));
            }

        function
                .addContent(EMPTY_LINE)
                .addContent(new ReturnStatement("titleRow"));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getTitleFunctionStart() {
        return new FunctionDeclaration("getTitleRow", "TrTag")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addContent(new VarDeclaration("TrTag", "titleRow", new FunctionCall("getDefaultStartOfTitleRow")))
                .addContent(EMPTY_LINE)
                .addContent(new IfBlock(new Condition("displayId"))
                        .addContent(new FunctionCall("child", "titleRow")
                                .byItself()
                                .addArgument(new FunctionCall("getIdTitleCell"))));
    }

    private FunctionCall getLabelTitleCellFunctionCall(Column column) {
        return new FunctionCall("add" + chopID(column.getJavaName()) + "TitleCellsTo")
                .byItself()
                .addArgument("titleRow");
    }

    private FunctionCall getTitleCellFunctionCall(Column column) {
        return new FunctionCall("child", "titleRow")
                .byItself()
                .addArgument(new FunctionCall("get" + capitalize(column.getJavaName()) + "TitleCell"));
    }

    private void addTitleCellFunctions() {
        for (Column column: columns.getList())
            if (!column.isItemOrder()) {
                if (column.isLabelReference())
                    addLabelTitleFunctions(column);
                else
                    addTitleCellFunction(column);
            }
    }

    private void addLabelTitleFunctions(Column column) {
        String name = column.getJavaName();
        String labelName = chopID(name);

        javaClass
                .addContent(new FunctionDeclaration("add" + labelName + "TitleCellsTo")
                        .addArgument(new FunctionArgument("TrTag", "titleRow"))
                        .addContent(new IfBlock(new Condition("displayAllLanguages"))
                                .addContent(new ForEach(
                                        "DbBeanLanguage",
                                        "dbBeanLanguage",
                                        new FunctionCall("getAllActiveLanguages", "LabelManager"))
                                        .addContent(new FunctionCall("child", "titleRow")
                                                .byItself()
                                                .addArgument(new FunctionCall("get" + labelName + "TitleCell")
                                                        .addArgument("dbBeanLanguage"))))
                                .elseClause(new ElseBlock()
                                        .addContent(new FunctionCall("child", "titleRow")
                                                .byItself()
                                                .addArgument(new FunctionCall("get" + labelName + "TitleCell")
                                                        .addArgument(new FunctionCall("getLanguage", "dbBeanLocalization")))))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("get" + labelName + "TitleCell", "ThTag")
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new ReturnStatement(new FunctionCall("getTitleCell")
                                .addArgument(new OperatorExpression(
                                        new FunctionCall("getIso", "dbBeanLanguage"),
                                        quickQuote(labelName),
                                        OperatorExpression.Operator.ADD))
                                .addArgument(new OperatorExpression(
                                        new OperatorExpression(
                                                new FunctionCall("getTitle").addArgument(quickQuote(name)),
                                                quickQuote(" "),
                                                OperatorExpression.Operator.ADD),
                                        new FunctionCall("getCapIso", "dbBeanLanguage"),
                                        OperatorExpression.Operator.ADD)))))
                .addContent(EMPTY_LINE);
    }

    private void addTitleCellFunction(Column column) {
        String name = column.getJavaName();

        javaClass
                .addContent(new FunctionDeclaration("get" + capitalize(name) + "TitleCell", "ThTag")
                        .addContent(new ReturnStatement(new FunctionCall("getTitleCell").addArgument(quickQuote(name)))))
                .addContent(EMPTY_LINE);
    }

    private void addDataFunctions() {
        addGetDataFunction();
        addInventoryFunction();
        addLineCountFunction();
    }

    private void addGetDataFunction() {
        javaClass
                .addContent(new FunctionDeclaration("getData", "List<TrTag>")
                        .annotate("@Override")
                        .visibility(Visibility.PROTECTED)
                        .addContent(new ReturnStatement(new FunctionCall(
                                "collect",
                                new FunctionCall("map", new FunctionCall("stream", new FunctionCall("get" + beanName + "Inventory")))
                                        .addArgument("this::getTableLine"))
                                .addArgument(new FunctionCall("toList", "Collectors")))))
                .addContent(EMPTY_LINE);
    }

    private void addInventoryFunction() {
        javaClass
                .addContent(new FunctionDeclaration("get" + beanName + "Inventory", "List<" + beanName + ">")
                        .addContent(new ReturnStatement(new FunctionCall("getBeansInLocalOrder")
                                .addArgument(new FunctionCall("getAll", beanName)))))
                .addContent(EMPTY_LINE);
    }

    private void addLineCountFunction() {
        javaClass
                .addContent(new FunctionDeclaration("getLineCount", "long")
                        .annotate("@Override")
                        .visibility(Visibility.PROTECTED)
                        .addContent(new ReturnStatement(new FunctionCall("getCount", beanName))))
                .addContent(EMPTY_LINE);
    }

    private void addDataLineFunction() {
        String beanDataClass = "Formatted" + beanName + "Data";
        String beanDataVarName = beanVarName + "Data";

        var function = new FunctionDeclaration("addDataToLine", "<B extends DbBeanInterface> void")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("TrTag", "line"))
                .addArgument(new FunctionArgument("B", beanVarName))
                .addContent(new VarDeclaration(
                        beanDataClass,
                        beanDataVarName,
                        new ObjectCreation(beanDataClass)
                                .addArguments("(" + beanName + ") " + beanVarName, "dbBeanLocalization")));

        for (Column column: columns.getList())
            if (!column.isSpecial())
                function.addContent(getDataCellFunctionCall(column, beanDataVarName));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private FunctionCall getDataCellFunctionCall(Column column, String beanDataVarName) {
        String name = column.getJavaName();

        if (column.isLabelReference())
            return new FunctionCall("add" + chopID(name) + "DataCellsTo").byItself().addArguments("line", beanDataVarName);

        return new FunctionCall("child", "line").byItself()
                .addArgument(new FunctionCall("get" + capitalize(name) + "TableCell").addArgument(beanDataVarName));
    }

    private void addDataCellFunctions() {
        for (Column column: columns.getList())
            if (!column.isSpecial()) {
                if (column.isLabelReference())
                    addDLabelDataCellFunctions(column);
                else
                    addDataCellFunction(column);
            }
    }

    private void addDLabelDataCellFunctions(Column column) {
        String name = column.getJavaName();

        javaClass
                .addContent(new FunctionDeclaration("add" + chopID(name) + "DataCellsTo")
                        .addArgument(new FunctionArgument("TrTag", "dataRow"))
                        .addArgument(new FunctionArgument("Formatted" + beanName + "Data", beanVarName))
                        .addContent(new IfBlock(new Condition("displayAllLanguages"))
                                .addContent(new ForEach(
                                        "DbBeanLanguage",
                                        "dbBeanLanguage",
                                        new FunctionCall("getAllActiveLanguages", "LabelManager"))
                                        .addContent(new FunctionCall("child", "dataRow")
                                                .byItself()
                                                .addArgument(new FunctionCall("get" + chopID(name) + "TableCell")
                                                        .addArguments("dbBeanLanguage", beanVarName))))
                                .elseClause(new ElseBlock()
                                        .addContent(new FunctionCall("child", "dataRow")
                                                .byItself()
                                                .addArgument(new FunctionCall("get" + chopID(name) + "TableCell")
                                                        .addArgument(new FunctionCall("getLanguage", "dbBeanLocalization"))
                                                        .addArgument(beanVarName))))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("get" + chopID(name) + "TableCell", "TdTag")
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addArgument(new FunctionArgument("Formatted" + beanName + "Data", beanVarName))
                        .addContent(new ReturnStatement(new FunctionCall("getTableCell")
                                .addArgument(new FunctionCall("createTextCellDefinition", "MasterTableCellDefinition")
                                        .addArgument(new OperatorExpression(
                                                new FunctionCall("getIso", "dbBeanLanguage"),
                                                quickQuote(chopID(name)),
                                                OperatorExpression.Operator.ADD))
                                        .addArgument(new FunctionCall("get" + chopID(name), beanVarName)
                                                .addArgument("dbBeanLanguage"))))))
                .addContent(EMPTY_LINE);
    }

    private void addDataCellFunction(Column column) {
        String type = column.getJavaType();
        String name = column.getJavaName();

        var function = new FunctionDeclaration("get" + capitalize(name) + "TableCell", "TdTag")
                .addArgument(new FunctionArgument("Formatted" + beanName + "Data", beanVarName));

        if (column.isFileReference())
            function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                    .addArgument(new FunctionCall("createTextCellDefinition", "MasterTableCellDefinition")
                            .addArgument(quickQuote(name))
                            .addArgument(new FunctionCall("get" + chopID(name) + "Link", beanVarName)))));
        else if (column.isLabelReference())
            throw new AssertionError("Labels cannot show up here");
        else if (column.isBeanReference())
            function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                    .addArgument(new FunctionCall("createTextCellDefinition", "MasterTableCellDefinition")
                            .addArgument(quickQuote(name))
                            .addArgument(new FunctionCall("get" + chopID(name), beanVarName)))));
        else {
            switch (type) {
                case "Boolean":
                    function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                            .addArgument(new FunctionCall("createBooleanCellDefinition", "MasterTableCellDefinition")
                                    .addArgument(quickQuote(name))
                                    .addArgument(new FunctionCall("is" + capitalize(name), beanVarName))
                                    .addArgument(new FunctionCall("is" + capitalize(name), new FunctionCall("get" + beanName, beanVarName)))
                                    .addArguments("yesValue", "noValue"))));
                    break;
                case "Integer":
                case "Long":
                    function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                            .addArgument(new FunctionCall("createIntegerCellDefinition", "MasterTableCellDefinition")
                                    .addArgument(quickQuote(name))
                                    .addArgument(new FunctionCall("get" + capitalize(name), beanVarName))
                                    .addArgument(new FunctionCall("get" + capitalize(name), new FunctionCall("get" + beanName, beanVarName)))
                                    .addArgument("zeroFilledMaxDigits"))));
                    break;
                case "String":
                    function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                            .addArgument(new FunctionCall("createTextCellDefinition", "MasterTableCellDefinition")
                                    .addArgument(quickQuote(name))
                                    .addArgument(new FunctionCall("get" + capitalize(name), beanVarName)))));
                    break;
                case "Date":
                    function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                            .addArgument(new FunctionCall("createDateCellDefinition", "MasterTableCellDefinition")
                                    .addArgument(quickQuote(name))
                                    .addArgument(new FunctionCall("get" + capitalize(name), beanVarName))
                                    .addArgument(new FunctionCall("get" + capitalize(name), new FunctionCall("get" + beanName, beanVarName))))));
                    break;
                case "Time":
                    function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                            .addArgument(new FunctionCall("createTimeCellDefinition", "MasterTableCellDefinition")
                                    .addArgument(quickQuote(name))
                                    .addArgument(new FunctionCall("get" + capitalize(name), beanVarName))
                                    .addArgument(new FunctionCall("get" + capitalize(name), new FunctionCall("get" + beanName, beanVarName))))));
                    break;
                case "Timestamp":
                    function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                            .addArgument(new FunctionCall("createTimestampCellDefinition", "MasterTableCellDefinition")
                                    .addArgument(quickQuote(name))
                                    .addArgument(new FunctionCall("get" + capitalize(name), beanVarName))
                                    .addArgument(new FunctionCall("get" + capitalize(name), new FunctionCall("get" + beanName, beanVarName))))));
                    break;
                case "Money":
                    function.addContent(new ReturnStatement(new FunctionCall("getTableCell")
                            .addArgument(new FunctionCall("createMoneyCellDefinition", "MasterTableCellDefinition")
                                    .addArgument(quickQuote(name))
                                    .addArgument(new FunctionCall("get" + capitalize(name), beanVarName))
                                    .addArgument(new FunctionCall("get" + capitalize(name), new FunctionCall("get" + beanName, beanVarName)))
                                    .addArgument("zeroFilledMaxDigits"))));
                    break;
                default:
                    throw new AssertionError("Unknown/unsupported type: " + type);
            }
        }

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

}
