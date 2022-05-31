package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;

import static org.beanmaker.v2.util.Strings.quickQuote;

public class BeanParametersBaseSourceFile extends BeanCodeWithDBInfo {

    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DbBeanLanguage", "DbBeanLocalization", "DbBeanParameters");

    public BeanParametersBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanParametersBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "ParametersBase", columns, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(BM_RUNTIME_IMPORTS);

        importsManager.addImport("java.util.List");

        if (columns.hasLabels())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanRequiredLanguages");
        if (columns.hasItemOrder())
            importsManager.addImport("org.beanmaker.v2.runtime.ItemOrderManager");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().implementsInterface("DbBeanParameters");
        applySealedModifier(beanName + "Parameters");
    }

    @Override
    protected void addStaticProperties() {
        if (columns.hasLabels())
            javaClass
                    .addContent(new VarDeclaration(
                            "DbBeanRequiredLanguages",
                            "DBBEAN_REQUIRED_LANGUAGES",
                            new ObjectCreation("DbBeanRequiredLanguages")
                                    .addArgument(new FunctionCall("getAllActiveLanguages", "LabelManager")))
                            .visibility(Visibility.PRIVATE)
                            .markAsStatic()
                            .markAsFinal())
                    .addContent(EMPTY_LINE);

        if (columns.hasItemOrder()) {
            var itemOrderManager = new ObjectCreation("ItemOrderManager").addArgument(quickQuote(tableName));
            var itemOrder = columns.getItemOrderField();
            if (!itemOrder.isUnique())
                itemOrderManager.addArgument(quickQuote(itemOrder.getItemOrderAssociatedField()));

            javaClass
                    .addContent(new VarDeclaration("ItemOrderManager", "ITEM_ORDER_MANAGER", itemOrderManager)
                            .visibility(Visibility.PRIVATE)
                            .markAsStatic()
                            .markAsFinal())
                    .addContent(EMPTY_LINE);
        }
    }

    @Override
    protected void addCoreFunctionality() {
        addLocalizationFunctions();
        addItemOrderManagerFunction();
        addDatabaseFunctions();
        addNamingFunction();
        addOrderingFunction();
        addLabelFunctions();
    }

    private void addLocalizationFunctions() {
        javaClass
                .addContent(getBaseLocalizationFunction()
                        .addContent(new ReturnStatement(getLocalizationObject())))
                .addContent(EMPTY_LINE)
                .addContent(getBaseLocalizationFunction()
                        .addArgument(new FunctionArgument("DbBeanLanguage", "language"))
                        .addContent(new ReturnStatement(getLocalizationObject().addArgument("language"))))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getBaseLocalizationFunction() {
        return getFunctionDeclaration("DbBeanLocalization", "getLocalization");
    }

    private ObjectCreation getLocalizationObject() {
        return new ObjectCreation("DbBeanLocalization")
                .addArgument(new FunctionCall("getBasicFunctions", "LabelManager"))
                .addArgument(quickQuote(bundleName))
                .addArgument(quickQuote(beanName));
    }

    private void addItemOrderManagerFunction() {
        if (columns.hasItemOrder())
            javaClass
                    .addContent(getFunctionDeclaration("ItemOrderManager", "getItemOrderManager")
                            .addContent(new ReturnStatement("ITEM_ORDER_MANAGER")))
                    .addContent(EMPTY_LINE);
    }

    private void addDatabaseFunctions() {
        javaClass
                .addContent(getFunctionDeclaration("String", "getDatabaseTableName")
                        .addContent(new ReturnStatement(quickQuote(tableName))))
                .addContent(EMPTY_LINE)
                .addContent(getFunctionDeclaration("String", "getDatabaseFieldList")
                        .addContent(new ReturnStatement(quickQuote(getTableFieldList()))))
                .addContent(EMPTY_LINE);
    }

    private String getTableFieldList() {
        var list = new StringBuilder();

        for (Column column: columns.getList())
            list.append(tableName).append(".").append(column.getSqlName()).append(", ");
        list.delete(list.length() - 2, list.length());

        return list.toString();
    }

    private void addNamingFunction() {
        javaClass
                .addContent(getFunctionDeclaration("List<String>", "getNamingFields")
                        .addContent(new ReturnStatement(new FunctionCall("of", "List")
                                .addArgument(quickQuote(columns.getNamingField())))))
                .addContent(EMPTY_LINE);
    }

    private void addOrderingFunction() {
        var listCreation = new FunctionCall("of", "List");
        for (String field: columns.getOrderByFields())
            listCreation.addArgument(quickQuote(field));

        javaClass
                .addContent(getFunctionDeclaration("List<String>", "getOrderingFields")
                        .addContent(new ReturnStatement(listCreation)))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getFunctionDeclaration(String returnType, String functionName) {
        return new FunctionDeclaration(functionName, returnType).annotate("@Override").visibility(Visibility.PUBLIC);
    }

    private void addLabelFunctions() {
        for (var label: columns.getLabels())
            javaClass
                    .addContent(new FunctionDeclaration(
                            "getRequiredLanguagesFor" + chopID(label.getJavaName()),
                            "DbBeanRequiredLanguages")
                            .visibility(Visibility.PUBLIC)
                            .addContent(new ReturnStatement("DBBEAN_REQUIRED_LANGUAGES")))
                    .addContent(EMPTY_LINE);
    }

}
