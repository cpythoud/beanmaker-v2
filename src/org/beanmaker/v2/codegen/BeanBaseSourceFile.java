package org.beanmaker.v2.codegen;

import org.jcodegen.java.Assignment;
import org.jcodegen.java.CatchBlock;
import org.jcodegen.java.Condition;
import org.jcodegen.java.ConstructorDeclaration;
import org.jcodegen.java.ExceptionThrow;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.LambdaExpression;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.TryBlock;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;
import org.jcodegen.java.WhileBlock;

import java.util.List;
import java.util.Set;

import static org.beanmaker.v2.util.Strings.capitalize;
import static org.beanmaker.v2.util.Strings.quickQuote;

public class BeanBaseSourceFile extends BeanCodeWithDBInfo {

    private static final List<String> JAVA_SQL_IMPORTS = createImportList("java.sql", "ResultSet", "SQLException");
    private static final List<String> JAVA_UTIL_IMPORTS = createImportList("java.util", "ArrayList", "List");
    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DBUtil", "DbBeanLanguage", "ToStringMaker");
    private static final List<String> SQL_IMPORTS =
            createImportList("org.dbbeans.sql", "DBQuerySetup", "DBTransaction", "SQLRuntimeException");

    private final Set<String> types;

    public BeanBaseSourceFile(String beanName, String packageName, Columns columns) {
        super(beanName, packageName, "Base", columns);

        types = columns.getJavaTypes();

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(JAVA_SQL_IMPORTS, JAVA_UTIL_IMPORTS, BM_RUNTIME_IMPORTS, SQL_IMPORTS);

        if (types.contains("String"))
            importsManager.addImport("org.beanmaker.v2.util.Strings");
        if (types.contains("Date"))
            importsManager.addImport("java.sql.Date");
        if (types.contains("Time"))
            importsManager.addImport("java.sql.Time");
        if (types.contains("Timestamp"))
            importsManager.addImport("java.sql.Timestamp");
        if (types.contains("Money"))
            importsManager.addImport("org.beanmaker.v2.util.Money");

        if (columns.hasItemOrder())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanWithItemOrder");
        else
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanInterface");
        if (columns.hasLastUpdate())
            throw new UnsupportedOperationException("last_update field not supported in current implementation");
        if (columns.hasLabels()) {
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLabel");
            if (columns.hasLabelField())
                importsManager.addImport("org.beanmaker.v2.runtime.DbBeanMultilingual");
        }
        if (columns.hasFiles())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFile");

        importsManager.addStaticImport(packageName + ".DbBeans.dbAccess");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract();

        if (columns.hasItemOrder())
            javaClass.implementsInterface("DbBeanWithItemOrder");
        else
            javaClass.implementsInterface("DbBeanInterface");

        if (columns.hasLabelField())
            javaClass.implementsInterface("DbBeanMultilingual");
    }

    @Override
    protected void addProperties() {
        for (var column: columns.getList())
            addProperty(column.getJavaType(), column.getJavaName(), true, Visibility.PRIVATE);

        newLine();
    }

    @Override
    protected void addConstructors() {
        addIDConstructor();
        addFieldsConstructor();
        addRSConstructor();
    }

    private void addIDConstructor() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new FunctionCall("this")
                                .byItself()
                                .addArgument(new FunctionCall(
                                        "orElseThrow",
                                        new FunctionCall("getInitResultSet", "DBUtil")
                                                .addArguments("id", parametersInstanceExpression, "dbAccess"))
                                        .addArgument(new LambdaExpression()
                                                .addContent(new ObjectCreation("IllegalArgumentException")
                                                        .addArgument(badIDExceptionMessage))))))
                .addContent(EMPTY_LINE);
    }

    private void addFieldsConstructor() {
        ConstructorDeclaration constructor = javaClass.createConstructor();
        for (Column column: columns.getList()) {
            String type = column.getJavaType();
            String fieldName = column.getJavaName();
            constructor.addArgument(new FunctionArgument(type, fieldName));
            if (TEMPORAL_TYPES.contains(type))
                constructor.addContent(new Assignment(
                        "this." + fieldName,
                        new FunctionCall("copy", "DBUtil").addArgument(fieldName)));
            else
                constructor.addContent(new Assignment("this." + fieldName, fieldName));
        }

        javaClass.addContent(constructor).addContent(EMPTY_LINE);
    }

    private void addRSConstructor() {
        FunctionCall thisCall = new FunctionCall("this").byItself();
        int index = 0;
        for (Column column: columns.getList())
            thisCall.addArgument(getEncapsulatedDBUtilRSFunctionCall(column, ++index));

        javaClass
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument("ResultSet", "rs"))
                        .addContent(thisCall))
                .addContent(EMPTY_LINE);
    }

    // TODO: déplacer cette classe dans la sous-classe appropriée !!!
    private FunctionCall getEncapsulatedDBUtilRSFunctionCall(Column column, int index) {
        String type = column.getJavaType();
        String functionName;
        if (column.isBeanReference())
            functionName = "getBeanID";
        else if (column.isItemOrder())
            functionName = "getItemOrder";
        else if (type.equals("Integer"))
            functionName = "getInt";
        else
            functionName = "get" + type;

        var functionCall = new FunctionCall(functionName, "DBUtil").addArguments("rs", Integer.toString(index));
        if (type.equals("Money"))
            functionCall.addArgument(formatterInstanceExpression);

        return functionCall;
    }

    @Override
    protected void addCoreFunctionality() {
        addRefreshFromDatabaseFunction();
        addEqualsFunction();
        addHashCodeFunction();
        addToStringFunction();
        addGetters();
        addEmptyChecks();
        addListAndCountOfBeansInRelationshipFunctions();
        addNamingFunction();
        addInventoriesAndCountFunctions();
        addIDCheckFunctions();
        addListFunction();
    }

    private void addRefreshFromDatabaseFunction() {
        javaClass
                .addContent(new FunctionDeclaration("refreshFromDataBase", beanName)
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new ObjectCreation(beanName).addArgument("id"))))
                .addContent(EMPTY_LINE);
    }

    private void addEqualsFunction() {
        javaClass
                .addContent(new FunctionDeclaration("equals", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .annotate("@Override")
                        .addArgument(new FunctionArgument("Object", "object"))
                        .addContent(new IfBlock(new Condition("object instanceof " + beanName))
                                .addContent(new ReturnStatement("((" + beanName + ") object).getId() == id")))
                        .addContent(EMPTY_LINE)
                        .addContent(new ReturnStatement("false")))
                .addContent(EMPTY_LINE);
    }

    private void addHashCodeFunction() {
        javaClass
                .addContent(new FunctionDeclaration("hashCode", "int")
                        .visibility(Visibility.PUBLIC)
                        .annotate("@Override")
                        .addContent(new ReturnStatement(
                                new FunctionCall("hashCode", "Long")
                                        .addArgument("id"))))
                .addContent(EMPTY_LINE);
    }

    private void addToStringFunction() {
        FunctionDeclaration function = new FunctionDeclaration("toString", "String")
                .visibility(Visibility.PUBLIC)
                .annotate("@Override")
                .addContent(new VarDeclaration(
                        "ToStringMaker",
                        "stringMaker",
                        new ObjectCreation("ToStringMaker").addArgument("this")));

        for (Column column: columns.getList())
            if (!column.isId()) {
                String field = column.getJavaName();
                function.addContent(new FunctionCall("addField", "stringMaker")
                        .byItself()
                        .addArguments(quickQuote(field), field));
            }

        function.addContent(new ReturnStatement(new FunctionCall("toString", "stringMaker")));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private void addGetters() {
        for (Column column: columns.getList())
            addGetter(column);
    }

    private void addGetter(Column column) {
        String type = (column.isId() || column.isItemOrder()) ? "long" : column.getJavaType();
        String fieldName = column.getJavaName();
        String getterPrefix = (type.equals("Boolean") || type.equals("boolean")) ? "is" : "get";

        var getter = new FunctionDeclaration(getterPrefix + capitalize(column.getJavaName()), type)
                .visibility(Visibility.PUBLIC);
        if (column.isId() || column.isItemOrder() || fieldName.equals("idLabel"))
            getter.annotate("@Override");
        if (TEMPORAL_TYPES.contains(type))
            getter.addContent(new ReturnStatement(new FunctionCall("copy", "DBUtil").addArgument(fieldName)));
        else
            getter.addContent(new ReturnStatement(fieldName));

        javaClass.addContent(getter).addContent(EMPTY_LINE);

        if (column.isBeanReference()) {
            if (column.isLabelReference())
                addLabelSpecificGetterFunctions(column);
            else if (column.isFileReference())
                addFileGetterFunction(column);
            else if (!column.isId())
                addBeanGetterFunction(column);
        }

        if (column.isItemOrder())
            addItemOrderEdgeStatusCheckFunctions();
    }

    private void addLabelSpecificGetterFunctions(Column column) {
        String name = column.getJavaName();
        FunctionDeclaration labelFunction =
                new FunctionDeclaration("get" + chopID(name), "DbBeanLabel")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(
                                new FunctionCall("get", "LabelManager")
                                        .addArgument(name)));

        FunctionDeclaration perLanguageLabelFunction =
                new FunctionDeclaration("get" + chopID(name), "String")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new ReturnStatement(
                                new FunctionCall("get", new FunctionCall(labelFunction.getName()))
                                        .addArgument("dbBeanLanguage")));

        if (name.equals("idLabel")) {
            labelFunction.annotate("@Override");
            perLanguageLabelFunction.annotate("@Override");
        }

        javaClass
                .addContent(labelFunction)
                .addContent(EMPTY_LINE)
                .addContent(perLanguageLabelFunction)
                .addContent(EMPTY_LINE);
    }

    private void addFileGetterFunction(Column column) {
        String name = column.getJavaName();
        javaClass
                .addContent(new FunctionDeclaration("get" + chopID(name), "DbBeanFile")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("get", "LocalFileManager")
                                .addArgument(name))))
                .addContent(EMPTY_LINE);
    }

    private void addBeanGetterFunction(Column column) {
        String type = column.getAssociatedBeanClass();
        String name = column.getJavaName();
        javaClass
                .addContent(new FunctionDeclaration("get" + chopID(name), type)
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new ObjectCreation(type).addArgument(name))))
                .addContent(EMPTY_LINE);
    }

    private void addItemOrderEdgeStatusCheckFunctions() {
        javaClass
                .addContent(new FunctionDeclaration("isFirstInItemOrder", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .annotate("@Override")
                        .addContent(new ReturnStatement(
                                new FunctionCall("isFirstInItemOrder", itemManagerRetrievalCall)
                                        .addArgument("this"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("isLastInItemOrder", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .annotate("@Override")
                        .addContent(new ReturnStatement(
                                new FunctionCall("isLastInItemOrder", itemManagerRetrievalCall)
                                        .addArguments("this", "dbAccess"))))
                .addContent(EMPTY_LINE);
    }

    private void addEmptyChecks() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addEmptyCheck(column);
    }

    private void addEmptyCheck(Column column) {
        String type = column.getJavaType();
        if (type.equals("String"))
            addStringEmptyCheck(column);
        else if (type.equals("long"))
            addBeanReferenceEmptyCheck(column);
        else
            addNullEmptyCheck(column);
    }

    private void addStringEmptyCheck(Column column) {
        javaClass
                .addContent(getIsEmptyFunctionDeclaration(column)
                        .addContent(new ReturnStatement(
                                new FunctionCall("isEmpty", "Strings")
                                        .addArgument(column.getJavaName()))))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getIsEmptyFunctionDeclaration(Column column) {
        return new FunctionDeclaration(getIsEmptyFunctionName(column), "boolean").visibility(Visibility.PUBLIC);
    }

    private void addBeanReferenceEmptyCheck(Column column) {
        javaClass
                .addContent(getIsEmptyFunctionDeclaration(column)
                        .addContent(new ReturnStatement(column.getJavaName() + " == 0")))
                .addContent(EMPTY_LINE);
    }

    private void addNullEmptyCheck(Column column) {
        javaClass
                .addContent(getIsEmptyFunctionDeclaration(column)
                        .addContent(new ReturnStatement(column.getJavaName() + " == null")))
                .addContent(EMPTY_LINE);
    }

    private void addListAndCountOfBeansInRelationshipFunctions() {
        for (OneToManyRelationship relationship: columns.getOneToManyRelationships()) {
            addListOfBeansInRelationshipFunction(relationship);
            addCountOfBeansInRelationshipFunction(relationship);
        }
    }

    private void addListOfBeansInRelationshipFunction(OneToManyRelationship relationship) {
        String type = relationship.getBeanClass();
        javaClass
                .addContent(new FunctionDeclaration("get" + capitalize(relationship.getJavaName()), "List<" + type + ">")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("getInventory", "DBUtil")
                                .addArgument(getParametersInstanceExpression(type))
                                .addArgument(quickQuote(relationship.getIdSqlName()))
                                .addArgument(new FunctionCall("getId"))
                                .addArgument(type + "::getList")
                                .addArgument("dbAccess"))))
                .addContent(EMPTY_LINE);
    }

    private void addCountOfBeansInRelationshipFunction(OneToManyRelationship relationship) {
        String type = relationship.getBeanClass();
        javaClass
                .addContent(new FunctionDeclaration("getCountFor" + capitalize(relationship.getJavaName()), "long")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("getInventorySize", "DBUtil")
                                .addArgument(getParametersInstanceExpression(type))
                                .addArgument(quickQuote(relationship.getIdSqlName()))
                                .addArgument(new FunctionCall("getId"))
                                .addArgument("dbAccess"))))
                .addContent(EMPTY_LINE);
    }

    private void addNamingFunction() {
        javaClass
                .addContent(new FunctionDeclaration("getNameForIdNamePairsAndTitles", "String")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "language"))
                        .addContent(new ReturnStatement(new FunctionCall("getHumanReadableTitle", "DBUtil")
                                .addArguments(parametersInstanceExpression, "id", "dbAccess"))))
                .addContent(EMPTY_LINE);
    }

    private void addInventoriesAndCountFunctions() {
        javaClass
                .addContent(new FunctionDeclaration("getAll", "List<" + beanName + ">")
                        .visibility(Visibility.PUBLIC)
                        .markAsStatic()
                        .addContent(new ReturnStatement(new FunctionCall("getAll")
                                .addArgument(parametersInstanceExpression + ".getOrderByFields()"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getAll", "List<" + beanName + ">")
                        .markAsStatic()
                        .addArgument(new FunctionArgument("String", "orderBy"))
                        .addContent(new ReturnStatement(new FunctionCall("getSelection")
                                .addArguments("null", "orderBy", "null"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getSelection", "List<" + beanName + ">")
                        .markAsStatic()
                        .addArgument(new FunctionArgument("String", "whereClause"))
                        .addContent(new ReturnStatement(new FunctionCall("getSelection")
                                .addArguments("whereClause", "null"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getSelection", "List<" + beanName + ">")
                        .markAsStatic()
                        .addArgument(new FunctionArgument("String", "whereClause"))
                        .addArgument(new FunctionArgument("DBQuerySetup", "setup"))
                        .addContent(new ReturnStatement(new FunctionCall("getSelection")
                                .addArguments("whereClause", parametersInstanceExpression + ".getOrderByFields()", "setup"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getSelection", "List<" + beanName + ">")
                        .markAsStatic()
                        .addArgument(new FunctionArgument("String", "whereClause"))
                        .addArgument(new FunctionArgument("String", "orderBy"))
                        .addArgument(new FunctionArgument("DBQuerySetup", "setup"))
                        .addContent(new ReturnStatement(new FunctionCall("getSelection", "DBUtil")
                                .addArguments(
                                        parametersInstanceExpression,
                                        "whereClause",
                                        "orderBy",
                                        "setup",
                                        beanName + "::getList",
                                        "dbAccess"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getSelectionCount", "long")
                        .markAsStatic()
                        .addArgument(new FunctionArgument("String", "whereClause"))
                        .addContent(new ReturnStatement(new FunctionCall("getSelectionCount")
                                .addArguments("whereClause", "null"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getSelectionCount", "long")
                        .markAsStatic()
                        .addArgument(new FunctionArgument("String", "whereClause"))
                        .addArgument(new FunctionArgument("DBQuerySetup", "setup"))
                        .addContent(new ReturnStatement(new FunctionCall("getSelectionCount", "DBUtil")
                                .addArguments(parametersInstanceExpression, "whereClause", "setup", "dbAccess"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getCount", "long")
                        .visibility(Visibility.PUBLIC)
                        .markAsStatic()
                        .addContent(new ReturnStatement(new FunctionCall("getFullCount", "DBUtil")
                                .addArguments(parametersInstanceExpression, "dbAccess"))))
                .addContent(EMPTY_LINE);
    }

    private void addIDCheckFunctions() {
        javaClass
                .addContent(new FunctionDeclaration("isIdOK", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .markAsStatic()
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new ReturnStatement(new FunctionCall("isIdOK", "DBUtil")
                                .addArguments(parametersInstanceExpression, "id", "dbAccess"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("isIdOK", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .markAsStatic()
                        .addArgument(new FunctionArgument("long", "id"))
                        .addArgument(new FunctionArgument("DBTransaction", "transaction"))
                        .addContent(new ReturnStatement(new FunctionCall("isIdOK", "DBUtil")
                                .addArguments(parametersInstanceExpression, "id", "transaction"))))
                .addContent(EMPTY_LINE);
    }

    private void addListFunction() {
        var function = new FunctionDeclaration("getList", "List<" + beanName + ">")
                .visibility(Visibility.PUBLIC)
                .markAsStatic()
                .addArgument(new FunctionArgument("ResultSet", "rs"));

        function
                .addContent(VarDeclaration.createListDeclaration(beanName, "list"))
                .addContent(EMPTY_LINE)
                .addContent(new TryBlock()
                        .addContent(new WhileBlock(new Condition(new FunctionCall("next", "rs")))
                                .addContent(new FunctionCall("add", "list")
                                        .byItself()
                                        .addArgument(new ObjectCreation(beanName).addArgument("rs"))))
                        .addCatchBlock(new CatchBlock(new FunctionArgument("SQLException", "sqlex"))
                                .addContent(new ExceptionThrow("SQLRuntimeException").addArgument("sqlex"))))
                .addContent(EMPTY_LINE)
                .addContent(new ReturnStatement("list"));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

}
