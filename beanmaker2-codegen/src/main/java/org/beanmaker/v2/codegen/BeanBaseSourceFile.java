package org.beanmaker.v2.codegen;

import org.jcodegen.java.ArrayInitialization;
import org.jcodegen.java.Assignment;
import org.jcodegen.java.CatchBlock;
import org.jcodegen.java.Condition;
import org.jcodegen.java.ConstructorDeclaration;
import org.jcodegen.java.ElseBlock;
import org.jcodegen.java.ExceptionThrow;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.Lambda;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.TryBlock;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;
import org.jcodegen.java.WhileBlock;

import java.util.List;
import java.util.Set;

import static org.beanmaker.v2.util.Strings.quickQuote;

public class BeanBaseSourceFile extends BeanCodeWithDBInfo {

    private static final List<String> JAVA_SQL_IMPORTS = createImportList("java.sql", "ResultSet", "SQLException");
    private static final List<String> JAVA_UTIL_IMPORTS = createImportList("java.util", "ArrayList", "List");
    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DBUtil", "DbBeanLanguage", "ToStringMaker", "DbBeanInitializer");
    private static final List<String> SQL_IMPORTS =
            createImportList("org.dbbeans.sql", "DBQuerySetup", "DBTransaction", "SQLRuntimeException");

    private final Set<String> types;

    public BeanBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "Base", columns, projectParameters);

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
        if (columns.hasUniqueCodeField()) {
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanWithUniqueCode");
            importsManager.addImport("org.beanmaker.v2.runtime.dbutil.Codes");
            importsManager.addImport("java.util.Optional");
        }

        importsManager.addStaticImport(packageName + ".DbBeans.dbAccess");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract();
        applySealedModifier(beanName);

        if (columns.hasItemOrder())
            javaClass.implementsInterface("DbBeanWithItemOrder");
        else
            javaClass.implementsInterface("DbBeanInterface");

        if (columns.hasLabelField())
            javaClass.implementsInterface("DbBeanMultilingual");

        if (columns.hasUniqueCodeField())
            javaClass.implementsInterface("DbBeanWithUniqueCode");
    }

    @Override
    protected void addProperties() {
        for (var column: columns.getList())
            addProperty(column.getJavaType(), column.getJavaName(), true, null);

        newLine();
    }

    @Override
    protected void addConstructors() {
        addIDConstructor();
        addIDAndTransactionConstructor();
        addFieldsConstructor();
        addRSConstructor();
    }

    private void addIDConstructor() {
        var constructor = javaClass.createConstructor()
                .addArgument(new FunctionArgument("long", "id"));

        if (columns.getCount() > 2) {
            constructor.addContent(new FunctionCall("this").byItself().addArguments("id", "null"));
        } else {
            // ! this() call above would be ambiguous with only 2 fields (id & an other in the bean) !
            addLocalVariablesToConstructor(constructor);
            constructor.addContent(getInitializerFunctionCallForConstructor(getInitializerForConstructor(), false));
            addFieldsInitializationToConstructor(constructor);
        }

        javaClass.addContent(constructor).addContent(EMPTY_LINE);
    }

    private void addIDAndTransactionConstructor() {
        var constructor = javaClass.createConstructor()
                .addArgument(new FunctionArgument("long", "id"))
                .addArgument(new FunctionArgument("DBTransaction", "transaction"));

        addLocalVariablesToConstructor(constructor);

        var initializer = getInitializerForConstructor();

        constructor.addContent(new IfBlock(new Condition("transaction == null"))
                .addContent(getInitializerFunctionCallForConstructor(initializer, false))
                .elseClause(new ElseBlock().addContent(getInitializerFunctionCallForConstructor(initializer, true))));

        addFieldsInitializationToConstructor(constructor);

        javaClass.addContent(constructor).addContent(EMPTY_LINE);
    }

    private void addLocalVariablesToConstructor(ConstructorDeclaration constructor) {
        for (Column column: columns.getList()) {
            if (!column.isId()) {
                String type = column.getJavaType();
                String fieldName = column.getJavaName();
                constructor.addContent(new VarDeclaration(
                        type + "[]", fieldName,
                        new ArrayInitialization(type, 1))
                        .markAsFinal());
            }
        }
    }

    private Lambda getInitializerForConstructor() {
        var initializer = new Lambda().addLambdaParameter("rs");
        int index = 1;
        for (Column column: columns.getList()) {
            if (!column.isId()) {
                String fieldName = column.getJavaName();
                initializer.addContent(new Assignment(
                        fieldName + "[0]",
                        getEncapsulatedDBUtilRSFunctionCall(column, ++index)));
            }
        }

        return initializer;
    }

    private FunctionCall getInitializerFunctionCallForConstructor(Lambda initializer, boolean transactionBased) {
        return new FunctionCall("initialize", "DbBeanInitializer")
                .byItself()
                .addArgument("id")
                .addArgument(beanName + "Parameters.INSTANCE")
                .addArgument(transactionBased ? "transaction" : "dbAccess")
                .addArgument(initializer);
    }

    private void addFieldsInitializationToConstructor(ConstructorDeclaration constructor) {
        for (Column column: columns.getList()) {
            if (column.isId())
                constructor.addContent(new Assignment("this.id", "id"));
            else {
                String fieldName = column.getJavaName();
                constructor.addContent(new Assignment("this." + fieldName, fieldName + "[0]"));
            }
        }
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
        addListAndCountOfBeansInRelationshipFunctions(Visibility.PUBLIC);
        addNamingFunction();
        addInventoriesAndCountFunctions();
        addIDCheckFunctions();
        addListFunction();
        addUniqueCodeFunction();
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

    @Override
    protected void addStrGetter(Column column) { }

    @Override
    protected void addLabelSpecificGetterFunctions(Column column) {
        String fieldName = column.getJavaName();
        String functionName = "get" + chopID(fieldName);
        FunctionDeclaration labelFunction =
                new FunctionDeclaration(functionName, "DbBeanLabel")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(
                                new FunctionCall("get", "LabelManager")
                                        .addArgument(fieldName)));

        FunctionDeclaration perLanguageLabelFunction =
                new FunctionDeclaration(functionName, "String")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new ReturnStatement(
                                new FunctionCall("get", new FunctionCall(labelFunction.getName()))
                                        .addArgument("dbBeanLanguage")));

        FunctionDeclaration perLanguageSafeLabelFunction =
                new FunctionDeclaration(getSafeLabelFunctionName(functionName), "String")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new ReturnStatement(
                                new FunctionCall("getSafeValue", new FunctionCall(labelFunction.getName()))
                                        .addArgument("dbBeanLanguage")));

        if (fieldName.equals("idLabel")) {
            labelFunction.annotate("@Override");
            perLanguageLabelFunction.annotate("@Override");
            perLanguageSafeLabelFunction.annotate("@Override");
        }

        javaClass
                .addContent(labelFunction)
                .addContent(EMPTY_LINE)
                .addContent(perLanguageLabelFunction)
                .addContent(EMPTY_LINE)
                .addContent(perLanguageSafeLabelFunction)
                .addContent(EMPTY_LINE);
    }

    private void addGetters() {
        for (Column column: columns.getList())
            addGetter(column);
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

    private void addNamingFunction() {
        javaClass
                .addContent(new FunctionDeclaration("getNameForIdNamePairsAndTitles", "String")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "language"))
                        .addContent(new ReturnStatement(new FunctionCall("getHumanReadableTitle", "DBUtil")
                                .addArgument(parametersInstanceExpression)
                                .addArgument("id")
                                .addArgument("dbAccess")
                                .addArgument(new FunctionCall("getBasicFunctions", "LabelManager"))
                                .addArgument("language"))))
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

    private void addUniqueCodeFunction() {
        if (columns.hasUniqueCodeField()) {
            javaClass.addContent(new FunctionDeclaration("getFromCode", "Optional<" + beanName + ">")
                    .visibility(Visibility.PUBLIC)
                    .markAsStatic()
                    .addArgument(new FunctionArgument("String", "code"))
                    .addContent(new ReturnStatement(new FunctionCall("getBean", "Codes")
                            .addArgument(beanName + ".class")
                            .addArgument(beanName + "Parameters.INSTANCE")
                            .addArgument("code")
                            .addArgument("DbBeans.dbAccess"))))
                    .addContent(EMPTY_LINE);
        }
    }

}
