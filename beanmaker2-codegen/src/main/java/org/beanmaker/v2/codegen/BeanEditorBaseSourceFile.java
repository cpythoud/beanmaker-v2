package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.java.Assignment;
import org.jcodegen.java.Comparison;
import org.jcodegen.java.Condition;
import org.jcodegen.java.ConstructorDeclaration;
import org.jcodegen.java.ElseBlock;
import org.jcodegen.java.ExceptionThrow;
import org.jcodegen.java.Expression;
import org.jcodegen.java.ForEach;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.JavaClass;
import org.jcodegen.java.JavaCodeBlock;
import org.jcodegen.java.Lambda;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.OperatorExpression;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.StringOrCode;
import org.jcodegen.java.TernaryOperator;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.beanmaker.v2.util.Strings.camelize;
import static org.beanmaker.v2.util.Strings.capitalize;
import static org.beanmaker.v2.util.Strings.quickQuote;
import static org.beanmaker.v2.util.Strings.uncapitalize;

public class BeanEditorBaseSourceFile extends BeanCodeWithDBInfo {

    private static final List<String> JAVA_SQL_IMPORTS =
            createImportList("java.sql", "PreparedStatement", "ResultSet", "SQLException");
    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DBUtil", "FieldValidationResult", "FieldValidator", "ToStringMaker");
    private static final List<String> SQL_IMPORTS =
            createImportList("org.dbbeans.sql", "DBQuerySetup", "DBTransaction");

    private final Set<String> types;

    public BeanEditorBaseSourceFile(String beanName, String packageName, Columns columns) {
        super(beanName, packageName, null, "EditorBase", columns);

        types = columns.getJavaTypes();

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(JAVA_SQL_IMPORTS, BM_RUNTIME_IMPORTS, SQL_IMPORTS);

        importsManager.addImport("java.util.List");
        importsManager.addImport("java.util.function.Function");
        importsManager.addImport("org.beanmaker.v2.util.Strings");

        if (types.contains("Integer") || types.contains("Long"))
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        if (types.contains("Date")) {
            importsManager.addImport("java.sql.Date");
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        }
        if (types.contains("Time")) {
            importsManager.addImport("java.sql.Time");
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        }
        if (types.contains("Timestamp")) {
            importsManager.addImport("java.sql.Timestamp");
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        }
        if (types.contains("Money")) {
            importsManager.addImport("org.beanmaker.v2.util.Money");
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        }

        if (columns.hasLastUpdate())
            throw new UnsupportedOperationException("last_update field not supported in current implementation");


        if (columns.hasFiles()) {
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFile");
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
        }

        if (columns.hasOtherBeanReference())
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");

        importsManager.addStaticImport(packageName + ".DbBeans.dbAccess");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract();
        applySealedModifier(beanName + "Editor");

        columns.getItemOrderColumn().ifPresentOrElse(column -> {
            if (column.hasItemOrderAssociatedField()) {
                importsManager.addImport("org.beanmaker.v2.runtime.DbBeanEditorWithItemOrderAndSecondaryField");
                javaClass.extendsClass("DbBeanEditorWithItemOrderAndSecondaryField");
            } else {
                importsManager.addImport("org.beanmaker.v2.runtime.DbBeanEditorWithItemOrder");
                javaClass.extendsClass("DbBeanEditorWithItemOrder");
            }
            importsManager.addStaticImport(packageName + ".DbBeans.db");
        }, () -> {
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanEditor");
            javaClass.extendsClass("DbBeanEditor");
        });

        if (columns.hasLabels()) {
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLabel");
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");
            if (columns.hasLabelField()) {
                importsManager.addImport("org.beanmaker.v2.runtime.DbBeanMultilingual");
                javaClass.implementsInterface("DbBeanMultilingual");
            }
        }
    }

    @Override
    protected void addProperties() {
        for (var column: columns.getList()) {
            if (column.isId() || column.isItemOrder())
                continue;

            String type = column.getJavaType();
            String name = column.getJavaName();

            if (type.equals("String"))
                addProperty("String", name, false, new StringOrCode<>(EMPTY_STRING));
            else if (type.equals("Money")) {
                addProperty(
                        "Money",
                        name,
                        false,
                        new StringOrCode<>(new FunctionCall("withFormat", "Money.ZERO")
                                .addArgument(beanName + "Formatter.INSTANCE.getDefaultMoneyFormat()"))
                );
                addProperty(
                        "String",
                        name + "Str",
                        false,
                        new StringOrCode<>(new FunctionCall("toString", name))
                );
            } else
                addProperty(type, name, false, null);

            if (type.equals("Integer") || type.equals("Long") || TEMPORAL_TYPES.contains(type))
                addProperty("String", name + "Str", false, new StringOrCode<>(EMPTY_STRING));
            if (column.isLabelReference())
                addProperty("DbBeanLabel", uncapitalize(chopID(name)), false, null);
        }

        newLine();
    }

    @Override
    protected void addConstructors() {
        addNoParamConstructor();
        addIDConstructor();
        addBeanConstructor();
        addFieldsConstructor();
        addRSConstructor();
        addCopyDataFunction();
        addFieldsInitFunction();
        addBeanInitFunction();
        addSetIDFunction();
    }

    private void addNoParamConstructor() {
        javaClass
                .addContent(createConstructor())
                .addContent(EMPTY_LINE);
    }

    private void addIDConstructor() {
        javaClass
                .addContent(createConstructor()
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new FunctionCall("setId").byItself().addArgument("id")))
                .addContent(EMPTY_LINE);
    }

    private void addBeanConstructor() {
        javaClass
                .addContent(createConstructor()
                        .addArgument(new FunctionArgument(beanName, beanVarName))
                        .addContent(new FunctionCall("init").byItself().addArguments(beanVarName, "false")))
                .addContent(EMPTY_LINE);
    }

    private void addFieldsConstructor() {
        var constructor = createConstructor();
        var initCall = new FunctionCall("init").byItself();

        for (Column column: columns.getList()) {
            constructor.addArgument(new FunctionArgument(column.getJavaType(), column.getJavaName()));
            initCall.addArgument(column.getJavaName());
        }

        javaClass
                .addContent(constructor.addContent(initCall))
                .addContent(EMPTY_LINE);
    }

    private void addRSConstructor() {
        var initCall = new FunctionCall("init").byItself();

        int index = 0;
        for (Column column: columns.getList()) {
            String type = column.getJavaType();
            FunctionCall dbUtilCall;
            if (column.isBeanReference())
                dbUtilCall = new FunctionCall("getBeanID", "DBUtil");
            else if (column.isItemOrder())
                dbUtilCall = new FunctionCall("getItemOrder", "DBUtil");
            else if (type.equals("Integer"))
                dbUtilCall = new FunctionCall("getInt", "DBUtil");
            else
                dbUtilCall = new FunctionCall("get" + type, "DBUtil");

            dbUtilCall.addArguments("rs", Integer.toString(++index));
            if (type.equals("Money"))
                dbUtilCall.addArgument(beanName + "Formatter.INSTANCE");

            initCall.addArgument(dbUtilCall);
        }

        javaClass
                .addContent(createConstructor()
                        .addArgument(new FunctionArgument("ResultSet", "rs"))
                        .addContent(initCall))
                .addContent(EMPTY_LINE);
    }

    private ConstructorDeclaration createConstructor() {
        var superCall = new FunctionCall("super").byItself().addArgument(parametersInstanceExpression);

        if (columns.hasItemOrder())
            superCall.addArguments("dbAccess", "db");

        return javaClass.createConstructor().addContent(superCall);
    }

    private void addCopyDataFunction() {
        String beanEditorClass = beanName + "Editor";
        String beanEditorVar = uncapitalize(beanEditorClass);

        javaClass
                .addContent(new FunctionDeclaration("copyData", beanEditorClass)
                        .markAsStatic()
                        .addArgument(new FunctionArgument(beanName, beanVarName))
                        .addContent(VarDeclaration.declareAndInit(beanEditorClass, beanEditorVar))
                        .addContent(new FunctionCall("init", beanEditorVar)
                                .byItself()
                                .addArguments(beanVarName, "true"))
                        .addContent(new ReturnStatement(beanEditorVar)))
                .addContent(EMPTY_LINE);
    }

    private void addFieldsInitFunction() {
        var initFunction = new FunctionDeclaration("init");

        for (Column column: columns.getList()) {
            String type = column.getJavaType();
            String name = column.getJavaName();

            initFunction.addArgument(new FunctionArgument(type, name));
            if (type.equals("Integer") || type.equals("Long") || TEMPORAL_TYPES.contains(type) || type.equals("Money"))
                initFunction.addContent(new FunctionCall("set" + capitalize(name)).byItself().addArguments(name));
            else
                initFunction.addContent(new Assignment("this." + name, name));
        }

        javaClass.addContent(initFunction).addContent(EMPTY_LINE);
    }

    private void addBeanInitFunction() {
        var initCall = new FunctionCall("init").byItself();

        for (Column column: columns.getList()) {
            if (column.isId())
                initCall.addArgument(new TernaryOperator(
                        new Condition("copy"),
                        "0L",
                        new FunctionCall("getId", beanVarName))
                );
            else {
                String capName = capitalize(column.getJavaName());
                String functionName = column.getJavaType().equals("Boolean") ? "is" + capName : "get" + capName;
                initCall.addArgument(new FunctionCall(functionName, beanVarName));
            }
        }

        javaClass
                .addContent(new FunctionDeclaration("init")
                        .addArgument(new FunctionArgument(beanName, beanVarName))
                        .addArgument(new FunctionArgument("boolean", "copy"))
                        .addContent(initCall))
                .addContent(EMPTY_LINE);
    }

    private void addSetIDFunction() {
        javaClass
                .addContent(new FunctionDeclaration("setId")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new FunctionCall("init")
                                .byItself()
                                .addArgument(new ObjectCreation(beanName).addArgument("id"))
                                .addArgument("false")))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() {
        addToStringFunction();
        addSetters();
        addGetters();
        addLabelGetters();
        addRequiredTestFunctions();
        addUniqueTestFunctions();
        addListAndCountOfBeansInRelationshipFunctions(Visibility.PACKAGE_PRIVATE);
        addLabelManagementFunctions();
        addPreUpdateConversionsFunction();
        addDataValidationFunctions();
        addResetFunctions();
        addDatabaseFunctions();
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
                String type = column.getJavaType();
                String name = column.getJavaName();
                function.addContent(new FunctionCall("addField", "stringMaker")
                        .byItself()
                        .addArguments(quickQuote(name), name));
                if (type.equals("Integer") || type.equals("Long") || TEMPORAL_TYPES.contains(type) || type.equals("Money")) {
                    String extraFieldName = name + "Str";
                    function.addContent(new FunctionCall("addField", "stringMaker")
                            .byItself()
                            .addArguments(quickQuote(extraFieldName), extraFieldName));
                }
            }

        function.addContent(new ReturnStatement(new FunctionCall("toString", "stringMaker")));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private void addSetters() {
        for (Column column: columns.getList())
            if (!column.isId())
                addSetterFunctions(column);
    }

    private void addSetterFunctions(Column column) {
        if (column.isBeanReference()) {
            if (column.isLabelReference())
                addLabelSetterFunctions(column);
            else if (column.isFileReference())
                addFileSetterFunctions(column);
            else
                addBeanSetterFunctions(column);
        } else if (column.isItemOrder()) {
            if (!Strings.isEmpty(column.getItemOrderAssociatedField()))
                addItemOrderSecondaryFieldSetterFunction(column);
        } else {
            String type = column.getJavaType();
            if (type.equals("Integer") || type.equals("Long"))
                addNumericDataSetterFunctions(column);
            else if (TEMPORAL_TYPES.contains(type))
                addTemporalDataSetterFunctions(column);
            else if (type.equals("Money"))
                addMoneySetterFunctions(column);
            else
                addStandardSetterFunction(column);
        }
    }

    private void addLabelSetterFunctions(Column column) {
        String fieldName = column.getJavaName();
        String labelName = chopID(fieldName);

        javaClass
                .addContent(getStandardSetterFunction(column)
                        .addContent(new FunctionCall("init" + labelName)
                                .byItself()))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("set" + labelName)
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLabel", "label"))
                        .addContent(checkNonZeroID("label", "DbBeanLabel"))
                        .addContent(EMPTY_LINE)
                        .addContent(new FunctionCall("set" + capitalize(fieldName))
                                .byItself()
                                .addArgument(new FunctionCall("getId", "label"))))
                .addContent(EMPTY_LINE);
    }

    private void addFileSetterFunctions(Column column) {
        String fieldName = column.getJavaName();
        String fileName = chopID(fieldName);

        javaClass
                .addContent(getStandardSetterFunction(column))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("set" + fileName)
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanFile", "file"))
                        .addContent(checkNonZeroID("file", "DbBeanFile"))
                        .addContent(EMPTY_LINE)
                        .addContent(new Assignment(fieldName, new FunctionCall("getId", "file"))))
                .addContent(EMPTY_LINE);
    }

    private void addBeanSetterFunctions(Column column) {
        String beanClass = column.getAssociatedBeanClass();
        String beanVar = uncapitalize(chopID(column.getJavaName()));

        javaClass
                .addContent(getStandardSetterFunction(column))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("set" + capitalize(beanVar))
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument(beanClass, beanVar))
                        .addContent(checkNonZeroID(beanVar, beanClass))
                        .addContent(EMPTY_LINE)
                        .addContent(new Assignment(column.getJavaName(), new FunctionCall("getId", beanVar))))
                .addContent(EMPTY_LINE);
    }

    private IfBlock checkNonZeroID(String object, String beanClass) {
        return new IfBlock(new Condition(object + ".getId() == 0"))
                .addContent(new ExceptionThrow("IllegalArgumentException")
                        .addArgument(getUnitializedBeanArgumentErrorMessage(beanClass)));
    }

    private String getUnitializedBeanArgumentErrorMessage(String beanClass) {
        return quickQuote("Cannot accept uninitialized " + beanClass + " bean (id = 0) as argument.");
    }

    private void addItemOrderSecondaryFieldSetterFunction(Column column) {
        javaClass
                .addContent(new FunctionDeclaration("setItemOrderSecondaryFieldID")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("long", "secondaryFieldID"))
                        .addContent(new FunctionCall("setItemOrderSecondaryFieldID", "super")
                                .byItself()
                                .addArgument("secondaryFieldID"))
                        .addContent(getSecondaryFieldSetterCall(column)))
                .addContent(EMPTY_LINE);
    }

    private FunctionCall getSecondaryFieldSetterCall(Column column) {
        String functionName = "set" + capitalize(getItemOrderSecondaryFieldJavaName(column));
        return new FunctionCall(functionName).byItself().addArgument("secondaryFieldID");
    }

    private String getItemOrderSecondaryFieldJavaName(Column column) {
        // !!! We assume the name of the field is the suggested name from Beanmaker.
        // !!! This might prove incorrect at same point and need an extension of the Column class
        // !!! as well as an adjustment of the related user interfaces.
        return uncapitalize(camelize(column.getItemOrderAssociatedField()));
    }

    private void addNumericDataSetterFunctions(Column column) {
        String field = column.getJavaName();
        String fieldStr = field + "Str";

        javaClass
                .addContent(new FunctionDeclaration("set" + capitalize(field))
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument(column.getJavaType(), field))
                        .addContent(new Assignment("this." + field, field))
                        .addContent(new IfBlock(new Condition(field + " == null"))
                                .addContent(new Assignment(fieldStr, EMPTY_STRING))
                                .elseClause(new ElseBlock()
                                        .addContent(new Assignment(fieldStr, new FunctionCall("toString", field))))))
                .addContent(EMPTY_LINE)
                .addContent(getStandardSetterFunction("String", fieldStr))
                .addContent(EMPTY_LINE);
    }

    private void addTemporalDataSetterFunctions(Column column) {
        String type = column.getJavaType();
        String field = column.getJavaName();
        String fieldStr = field + "Str";

        javaClass
                .addContent(new FunctionDeclaration("set" + capitalize(field))
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument(type, field))
                        .addContent(new Assignment(
                                "this." + field,
                                new FunctionCall("copy", "DBUtil").addArgument(field)))
                        .addContent(new Assignment(
                                fieldStr,
                                new FunctionCall("convert" + type + "ToString", beanName + "Formatter.INSTANCE")
                                        .addArgument(field))))
                .addContent(EMPTY_LINE)
                .addContent(getStandardSetterFunction("String", fieldStr))
                .addContent(EMPTY_LINE);
    }

    private void addMoneySetterFunctions(Column column) {
        String field = column.getJavaName();
        String fieldStr = field + "Str";

        javaClass
                .addContent(new FunctionDeclaration("set" + capitalize(field))
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("Money", field))
                        .addContent(new IfBlock(new Condition(field + " == null"))
                                .addContent(new Assignment("this." + field, "null"))
                                .addContent(new Assignment(fieldStr, EMPTY_STRING))
                                .elseClause(new ElseBlock()
                                        .addContent(new Assignment(
                                                "this." + field,
                                                new FunctionCall("withFormat", field)
                                                        .addArgument(new FunctionCall(
                                                                "getDefaultMoneyFormat",
                                                                beanName + "Formatter.INSTANCE"))))
                                        .addContent(new Assignment(
                                                fieldStr,
                                                new FunctionCall("toString", field))))))
                .addContent(EMPTY_LINE)
                .addContent(getStandardSetterFunction("String", fieldStr))
                .addContent(EMPTY_LINE);
    }

    private void addStandardSetterFunction(Column column) {
        addStandardSetterFunction(column.getJavaType(), column.getJavaName());
    }

    private void addStandardSetterFunction(String type, String name) {
        javaClass
                .addContent(getStandardSetterFunction(type, name))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getStandardSetterFunction(Column column) {
        return getStandardSetterFunction(column.getJavaType(), column.getJavaName());
    }

    private FunctionDeclaration getStandardSetterFunction(String type, String name) {
        return new FunctionDeclaration("set" + capitalize(name))
                .visibility(Visibility.PUBLIC)
                .addArgument(new FunctionArgument(type, name))
                .addContent(new Assignment("this." + name, name));
    }

    private void addGetters() {
        for (Column column: columns.getList())
            if (!column.isId() && !column.isItemOrder())
                addGetter(column);

        columns.getItemOrderColumn().ifPresent(column -> {
            if (!Strings.isEmpty(column.getItemOrderAssociatedField()))
                javaClass
                        .addContent(new FunctionDeclaration("getItemOrderSecondaryFieldID", "long")
                                .annotate("@Override")
                                .visibility(Visibility.PUBLIC)
                                .addContent(new ReturnStatement(
                                        new FunctionCall("get" + capitalize(getItemOrderSecondaryFieldJavaName(column))))))
                        .addContent(EMPTY_LINE);
        });
    }

    @Override
    protected void addStrGetter(Column column) {
        String name = column.getJavaName() + "Str";

        javaClass
                .addContent(new FunctionDeclaration("get" + capitalize(name), "String")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(name)))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addLabelSpecificGetterFunctions(Column column) {
        String idName = column.getJavaName();
        String labelNameCap = chopID(idName);
        String labelName = uncapitalize(labelNameCap);

        FunctionDeclaration labelFunction =
                new FunctionDeclaration("get" + chopID(idName), "DbBeanLabel")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new VarDeclaration(
                                "DbBeanLabel",
                                "dbBeanLabel",
                                new FunctionCall("get", "LabelManager").addArgument(idName)))
                        .addContent(EMPTY_LINE)
                        .addContent(new IfBlock(new Condition(labelName + " == null"))
                                .addContent(new ReturnStatement("dbBeanLabel")))
                        .addContent(EMPTY_LINE)
                        .addContent(new ReturnStatement(
                                new FunctionCall("replaceData", "LabelManager")
                                        .addArguments("dbBeanLabel", labelName)));

        FunctionDeclaration perLanguageLabelFunction =
                new FunctionDeclaration("get" + labelNameCap, "String")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new FunctionCall("init" + labelNameCap).byItself())
                        .addContent(new ReturnStatement(
                                new FunctionCall("get", labelName).addArgument("dbBeanLanguage")));

        if (idName.equals("idLabel")) {
            labelFunction.annotate("@Override");
            perLanguageLabelFunction.annotate("@Override");
        }

        javaClass
                .addContent(labelFunction)
                .addContent(EMPTY_LINE)
                .addContent(perLanguageLabelFunction)
                .addContent(EMPTY_LINE);
    }

    private void addLabelGetters() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addLabelGetter(column);
    }

    private void addLabelGetter(Column column) {
        String field = column.getJavaName();

        javaClass
                .addContent(new FunctionDeclaration("get" + capitalize(field) + "Label", "String")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("getLabel", "dbBeanLocalization")
                                .addArgument(quickQuote(field)))))
                .addContent(EMPTY_LINE);
    }

    private void addRequiredTestFunctions() {
        for (Column column: columns.getList()) {
            if (!column.isItemOrder()) {
                addRequiredTestFunction(column);

                if (column.isLabelReference())
                    addPerLangLabelRequiredTestFunction(column);
            }
        }
    }

    private void addRequiredTestFunction(Column column) {
        javaClass
                .addContent(new FunctionDeclaration("is" + capitalize(column.getJavaName() + "Required"), "boolean")
                        .addContent(new ReturnStatement(column.isRequired() ? "true" : "false")))
                .addContent(EMPTY_LINE);
    }

    private void addPerLangLabelRequiredTestFunction(Column column) {
        javaClass
                .addContent(new FunctionDeclaration("is" + capitalize(column.getJavaName() + "Required"), "boolean")
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new ReturnStatement(new FunctionCall(
                                "isRequired",
                                new FunctionCall(
                                        "getRequiredLanguagesFor" + chopID(column.getJavaName()),
                                        beanName + "Parameters.INSTANCE"))
                                .addArgument("dbBeanLanguage"))))
                .addContent(EMPTY_LINE);
    }

    private void addUniqueTestFunctions() {
        for (Column column: columns.getList())
            addUniqueTestFunction(column);
    }

    private void addUniqueTestFunction(Column column) {
        javaClass
                .addContent(new FunctionDeclaration("is" + capitalize(column.getJavaName() + "ToBeUnique"), "boolean")
                        .addContent(new ReturnStatement(column.isUnique() ? "true" : "false")))
                .addContent(EMPTY_LINE);
    }

    private void addLabelManagementFunctions() {
        for (Column column: columns.getList())
            if (column.isLabelReference())
                addLabelManagementFunctions(column);
    }

    private void addLabelManagementFunctions(Column column) {
        String fieldName = column.getJavaName();
        String labelName = chopID(fieldName);
        String labelBean = uncapitalize(labelName);

        javaClass
                .addContent(new FunctionDeclaration("init" + labelName)
                        .visibility(Visibility.PRIVATE)
                        .addContent(new IfBlock(new Condition(labelBean + " == null"))
                                .addContent(new Assignment(labelBean, new FunctionCall("createInstance", "LabelManager")))
                                .addContent(new IfBlock(new Condition(fieldName + " > 0"))
                                        .addContent(new FunctionCall("setId", labelBean).byItself().addArgument(fieldName))
                                        .addContent(new FunctionCall("cacheLabelsFromDB", labelBean).byItself()))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("set" + labelName)
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addArgument(new FunctionArgument("String", "text"))
                        .addContent(new FunctionCall("init" + labelName).byItself())
                        .addContent(new FunctionCall("updateLater", labelBean).byItself().addArguments("dbBeanLanguage", "text")))
                .addContent(EMPTY_LINE);
    }

    private void addPreUpdateConversionsFunction() {
        var conversionFunction = new FunctionDeclaration("preUpdateConversions")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("DBTransaction", "transaction"))
                .addContent(new FunctionCall("preUpdateConversions", "super")
                        .byItself()
                        .addArgument("transaction"))
                .addContent(EMPTY_LINE);

        boolean functionRequired = false;
        for (Column column: columns.getList()) {
            String type = column.getJavaType();
            if (column.isSpecial() || column.hasAssociatedBean() || type.equals("Boolean") || type.equals("String"))
                continue;

            conversionFunction.addContent(getConversionOperation(column));
            functionRequired = true;
        }

        if (functionRequired)
            javaClass.addContent(conversionFunction).addContent(EMPTY_LINE);
    }

    private Assignment getConversionOperation(Column column) {
        String type = column.getJavaType();
        String name = column.getJavaName();
        String nameStr = name + "Str";

        switch (type) {
            case "Integer":
                return new Assignment(name, getNumericConversion("getIntVal", nameStr));
            case "Long":
                return new Assignment(name, getNumericConversion("getLongVal", nameStr));
            case "Date":
            case "Time":
            case "Timestamp":
                return new Assignment(name, getTemporalConversion(type, nameStr));
            case "Money":
                return new Assignment(name, getMoneyConversion(nameStr));
        }

        throw new AssertionError("No processing defined for type: " + type);
    }

    private FunctionCall getNumericConversion(String conversionFunction, String nameStr) {
        return new FunctionCall(conversionFunction, "Strings").addArgument(nameStr);
    }

    private FunctionCall getTemporalConversion(String type, String nameStr) {
        return new FunctionCall("convertStringTo" + type, beanName + "Formatter.INSTANCE")
                .addArgument(nameStr);
    }

    private ObjectCreation getMoneyConversion(String nameStr) {
        return new ObjectCreation("Money")
                .addArgument(nameStr)
                .addArgument(new FunctionCall("getDefaultMoneyFormat", beanName + "Formatter.INSTANCE"));
    }

    private void addDataValidationFunctions() {
        var columns = this.columns.getList().stream().filter(column -> !column.isSpecial()).collect(Collectors.toList());

        addDataOKFunction(columns);
        for (Column column: columns)
            addCheckDataFunctions(column);
        for (Column column: columns)
            addEmptyCheckFunction(column);
        for (Column column: columns)
            addValidationFunctionListFunctions(column);
        for (Column column: columns)
            addUnicityTestFunctions(column);
    }

    private void addDataOKFunction(List<Column> columns) {
        var function = new FunctionDeclaration("isDataOK", "boolean")
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("DBTransaction", "transaction"))
                .addContent(new FunctionCall("clearErrorMessages", "dbBeanLocalization").byItself())
                .addContent(EMPTY_LINE);

        var labels = columns.stream().filter(Column::isLabelReference).collect(Collectors.toList());
        for (Column label: labels)
            function.addContent(new FunctionCall("init" + chopID(label.getJavaName())).byItself());
        if (!labels.isEmpty())
            function.addContent(EMPTY_LINE);

        int index = 0;
        for (Column column: columns)
            function.addContent(getDataCheckCall(column, ++index));

        function.addContent(EMPTY_LINE).addContent(new ReturnStatement("ok"));
        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private JavaCodeBlock getDataCheckCall(Column column, int index) {
        String functionName = "checkDataFor" + capitalize(column.getJavaName());

        if (index == 1)
            return new VarDeclaration("boolean", "ok", new FunctionCall(functionName).addArgument("transaction"));

        return new Assignment("ok", functionName + "(transaction) && ok");
    }

    private void addCheckDataFunctions(Column column) {
        String functionName = "checkDataFor" + capitalize(column.getJavaName());

        javaClass
                .addContent(new FunctionDeclaration(functionName, "boolean")
                        .addContent(new ReturnStatement(new FunctionCall(functionName).addArgument("null"))))
                .addContent(EMPTY_LINE)
                .addContent(getCheckDataFunction(column))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getCheckDataFunction(Column column) {
        String field = column.getJavaName();
        String functionName = "checkDataFor" + capitalize(field);

        var function = new FunctionDeclaration(functionName, "boolean")
                .addArgument(new FunctionArgument("DBTransaction", "transaction"));

        if (!column.isLabelReference())
            function.addContent(new VarDeclaration(
                    "FieldValidator",
                    "validator",
                    new ObjectCreation("FieldValidator")
                            .addArgument("dbBeanLocalization")
                            .addArgument("id")
                            .addArgument(quickQuote(field))
                            .addArgument(getCheckDataFunctionFunctionCall(field, "get", "Label"))
                            .addArgument(getCheckDataFunctionFunctionCall(field, "is", "Empty"))
                            .addArgument(getCheckDataFunctionFunctionCall(field, "is", "Required"))
                            .addArgument(getCheckDataFunctionFunctionCall(field, "is", "ToBeUnique"))
                            .addArgument(new Condition(getCheckDataFunctionFunctionCall(field, "is", "ToBeUnique"), true)
                                    .orCondition(new Condition(getCheckDataFunctionFunctionCall(field, "is", "Unique")
                                            .addArgument("transaction"))))));


        String validationFunctionsFunctionName = "get" + capitalize(field) + "ValidationFunctions";
        var validationCall = new FunctionCall("validate", "validator")
                .addArgument(new FunctionCall(validationFunctionsFunctionName))
                .addArgument("transaction");

        if (column.isLabelReference()) {
            function
                    .addContent(new VarDeclaration("boolean", "ok", "true"))
                    .addContent(EMPTY_LINE)
                    .addContent(new ForEach(
                            "DbBeanLanguage",
                            "dbBeanLanguage",
                            new FunctionCall("getAllActiveLanguages", "LabelManager"))
                            .addContent(new VarDeclaration(
                                    "String",
                                    "iso",
                                    new FunctionCall("getCapIso", "dbBeanLanguage")))
                            .addContent(new VarDeclaration(
                                    "FieldValidator",
                                    "contentValidator",
                                    new ObjectCreation("FieldValidator")
                                            .addArgument("dbBeanLocalization")
                                            .addArgument("id")
                                            .addArgument("\"" + field + "\" + iso")
                                            .addArgument(new OperatorExpression(
                                                    getCheckDataFunctionFunctionCall(field, "get", "Label"),
                                                    "\" \" + iso",
                                                    OperatorExpression.Operator.ADD))
                                            .addArgument(new FunctionCall("isEmpty", "Strings")
                                                    .addArgument(new FunctionCall("get", uncapitalize(chopID(field)))
                                                            .addArgument("dbBeanLanguage")))
                                            .addArgument(getCheckDataFunctionFunctionCall(field, "is", "Required")
                                                    .addArgument("dbBeanLanguage"))
                                            .addArgument("false")
                                            .addArgument("true")))
                            .addContent(new Assignment(
                                    "ok",
                                    new Condition(
                                            new FunctionCall("validate", "contentValidator")
                                                    .addArgument(new FunctionCall(validationFunctionsFunctionName)
                                                            .addArgument("dbBeanLanguage"))
                                                    .addArgument("transaction"))
                                            .andCondition(new Condition("ok")))))
                    .addContent(EMPTY_LINE)
                    .addContent(new ReturnStatement("ok"));
        } else
            function.addContent(new ReturnStatement(validationCall));

        return function;
    }

    private FunctionCall getCheckDataFunctionFunctionCall(String field, String prefix, String suffix) {
        return new FunctionCall(prefix + capitalize(field) + suffix);
    }

    private void addEmptyCheckFunction(Column column) {
        javaClass
                .addContent(new FunctionDeclaration("is" + capitalize(column.getJavaName()) + "Empty", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(getEmptyFieldTest(column))))
                .addContent(EMPTY_LINE);
    }

    private JavaCodeBlock getEmptyFieldTest(Column column) {
        String type = column.getJavaType();
        String field = column.getJavaName();

        if (type.equals("long"))
            return new Comparison(field, "0");
        if (type.equals("Boolean"))
            return new Comparison(field, "null");
        if (type.equals("String"))
            return getEmptyStringCheck(field);

        return getEmptyStringCheck(field + "Str");
    }

    private FunctionCall getEmptyStringCheck(String arg) {
        return new FunctionCall("isEmpty", "Strings").addArgument(arg);
    }

    private void addValidationFunctionListFunctions(Column column) {
        String type = column.getJavaType();
        String name = column.getJavaName();
        var functionDeclaration = getValidationFunctionListFunctionDeclaration(name);

        if (column.isLabelReference())
            functionDeclaration.addContent(getLabelValidationFunctionList(column));
        else if (column.isFileReference() || column.hasAssociatedBean())
            functionDeclaration.addContent(getBeanReferenceValidationFunctionList(column));
        else if (TEMPORAL_TYPES.contains(type))
            functionDeclaration.addContent(getTemporalValidationFunctionList(column));
        else if (type.equals("Integer") || type.equals("Long"))
            functionDeclaration.addContent(getNumericValidationFunctionList(column));
        else if (type.equals("String") || type.equals("Boolean"))
            functionDeclaration.addContent(getEmptyValidationFunctionList());
        else if (type.equals("Money"))
            functionDeclaration.addContent(getMoneyValidationFunctionList(column));
        else
            throw new AssertionError("Don't know how to process column:" + column);

        javaClass.addContent(functionDeclaration).addContent(EMPTY_LINE);

        if (column.isLabelReference()) {
            importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLocalization");
            javaClass
                    .addContent(getValidationFunctionListFunctionDeclaration(name)
                            .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                            .addContent(getEmptyValidationFunctionList()))
                    .addContent(EMPTY_LINE);
        }
    }

    FunctionDeclaration getValidationFunctionListFunctionDeclaration(String fieldName) {
        return new FunctionDeclaration(
                "get" + capitalize(fieldName) + "ValidationFunctions",
                "List<Function<DBTransaction, FieldValidationResult>>");
    }

    private ReturnStatement getLabelValidationFunctionList(Column column) {
        var lambda = createValidationFunctionLambda();

        String name = column.getJavaName();
        String idOKObject = "LabelManager";  // TODO: inline

        lambda
                .addContent(new VarDeclaration("boolean", "ok", "true"))
                .addContent(new IfBlock(new Condition("id > 0"))
                        .forceBrackets(true)
                        .addContent(new IfBlock(new Condition("transaction == null"))
                                .addContent(new Assignment("ok", getBaseIDOKFunctionCall(idOKObject, name)))
                                .elseClause(new ElseBlock()
                                        .addContent(new Assignment("ok", getBaseIDOKFunctionCall(idOKObject, name).addArgument("transaction"))))))
                .addContent(EMPTY_LINE)
                .addContent(new IfBlock(new Condition("ok"))
                        .addContent(new ReturnStatement("FieldValidationResult.OK")))
                .addContent(EMPTY_LINE)
                .addContent(getDefaultFieldValidationErrorMessage(name));

        return getValidationFunctionsListOfLambdas(lambda);
    }

    private ReturnStatement getBeanReferenceValidationFunctionList(Column column) {
        var lambda = createValidationFunctionLambda();

        String name = column.getJavaName();
        String idOKObject;
        if (column.isFileReference())
            idOKObject = "LocalFileManager";
        else
            idOKObject = column.getAssociatedBeanClass();

        lambda
                .addContent(new VarDeclaration("boolean", "ok"))
                .addContent(new IfBlock(new Condition("transaction == null"))
                        .addContent(new Assignment("ok", getBaseIDOKFunctionCall(idOKObject, name)))
                        .elseClause(new ElseBlock()
                                .addContent(new Assignment("ok", getBaseIDOKFunctionCall(idOKObject, name).addArgument("transaction")))))
                .addContent(EMPTY_LINE)
                .addContent(new IfBlock(new Condition("ok"))
                        .addContent(new ReturnStatement("FieldValidationResult.OK")))
                .addContent(EMPTY_LINE)
                .addContent(getDefaultFieldValidationErrorMessage(name));

        return getValidationFunctionsListOfLambdas(lambda);
    }

    private FunctionCall getBaseIDOKFunctionCall(String idOKObject, String fieldName) {
        return new FunctionCall("isIdOK", idOKObject).addArgument(fieldName);
    }

    private ReturnStatement getTemporalValidationFunctionList(Column column) {
        var lambda = createValidationFunctionLambda();

        String name = column.getJavaName();

        lambda
                .addContent(new IfBlock(new Condition(
                        new FunctionCall("validate" + column.getJavaType() + "Format", beanName + "Formatter.INSTANCE")
                                .addArgument(name + "Str")))
                        .addContent(new ReturnStatement("FieldValidationResult.OK")))
                .addContent(EMPTY_LINE)
                .addContent(getDefaultFieldValidationErrorMessage(name));

        return getValidationFunctionsListOfLambdas(lambda);
    }

    private ReturnStatement getNumericValidationFunctionList(Column column) {
        importsManager.addImport("org.beanmaker.v2.runtime.FormatCheckHelper");
        var lambda = createValidationFunctionLambda();

        String type = column.getJavaType();
        String name = column.getJavaName();
        String function;
        switch (type) {
            case "Integer":
                function = "isIntNumber";
                break;
            case "Long":
                function = "isLongNumber";
                break;
            default:
                throw new AssertionError("Unsupported type: " + type);
        }

        lambda
                .addContent(new IfBlock(new Condition(
                        new FunctionCall(function, "FormatCheckHelper").addArgument(name + "Str")))
                        .addContent(new ReturnStatement("FieldValidationResult.OK")))
                .addContent(EMPTY_LINE)
                .addContent(getDefaultFieldValidationErrorMessage(name));

        return getValidationFunctionsListOfLambdas(lambda);
    }

    private ReturnStatement getEmptyValidationFunctionList() {
        importsManager.addImport("java.util.Collections");
        return new ReturnStatement(new FunctionCall("emptyList", "Collections"));
    }

    private ReturnStatement getMoneyValidationFunctionList(Column column) {
        var lambda = createValidationFunctionLambda();

        String name = column.getJavaName();

        lambda
                .addContent(new IfBlock(new Condition(
                        new FunctionCall("isValOK", beanName + "Formatter.INSTANCE.getDefaultMoneyFormat()")
                                .addArgument(name + "Str")))
                        .addContent(new ReturnStatement("FieldValidationResult.OK")))
                .addContent(EMPTY_LINE)
                .addContent(getDefaultFieldValidationErrorMessage(name));

        return getValidationFunctionsListOfLambdas(lambda);
    }

    private Lambda createValidationFunctionLambda() {
        return new Lambda().addLambdaParameter("transaction");
    }

    private ReturnStatement getDefaultFieldValidationErrorMessage(String name) {
        return new ReturnStatement(new FunctionCall("create", "FieldValidationResult")
                .addArgument(new OperatorExpression(
                        quickQuote(name),
                        "DbBeanLocalization.BAD_FORMAT_EXT",
                        OperatorExpression.Operator.ADD)));
    }

    private ReturnStatement getValidationFunctionsListOfLambdas(Lambda lambda) {
        return new ReturnStatement(new FunctionCall("of", "List").addArgument(lambda));
    }

    private void addUnicityTestFunctions(Column column) {
        String name = column.getJavaName();
        String functionName = "is" + capitalize(name) + "Unique";

        javaClass
                .addContent(getUnicityCheckFunctionDeclaration(functionName)
                        .addContent(new ReturnStatement(new FunctionCall(functionName).addArgument("null"))))
                .addContent(EMPTY_LINE)
                .addContent(getUnicityCheckFunctionDeclaration(functionName)
                        .addArgument(new FunctionArgument("DBTransaction", "transaction"))
                        .addContent(new IfBlock(new Condition("transaction == null"))
                                .addContent(getUnicityCheckResult(column, false)))
                        .addContent(EMPTY_LINE)
                        .addContent(getUnicityCheckResult(column, true)))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getUnicityCheckFunctionDeclaration(String functionName) {
        return new FunctionDeclaration(functionName, "boolean");
    }

    private ReturnStatement getUnicityCheckResult(Column column, boolean transaction) {
        return new ReturnStatement(new FunctionCall("checkUnicity", "DBUtil")
                .addArgument(beanName + "Parameters.INSTANCE")
                .addArgument(quickQuote(column.getSqlName()))
                .addArgument(getUnicityCheckValue(column))
                .addArgument("id")
                .addArgument(transaction ? "transaction" : "dbAccess"));
    }

    private String getUnicityCheckValue(Column column) {
        StringOrCode<Expression> value;
        String type = column.getJavaType();
        String nameStr = column.getJavaName() + "Str";

        value = switch (type) {
            case "Integer" -> new StringOrCode<>(getNumericConversion("getIntVal", nameStr));
            case "Long" -> new StringOrCode<>(getNumericConversion("getLongVal", nameStr));
            case "Date", "Time", "Timestamp" -> new StringOrCode<>(getTemporalConversion(type, nameStr));
            case "Money" -> new StringOrCode<>(getMoneyConversion(nameStr));
            default -> new StringOrCode<>(column.getJavaName());
        };

        return value.toString();
    }

    private void addResetFunctions() {
        var labels = columns.getLabels();

        var resetFunction =
                new FunctionDeclaration("reset").annotate("@Override").visibility(Visibility.PUBLIC);

        if (!labels.isEmpty()) {
            for (Column label: labels)
                resetFunction.addContent(new FunctionCall("init" + chopID(label.getJavaName())).byItself());
            resetFunction.addContent(EMPTY_LINE);
        }

        for (Column column: columns.getList()) {
            if (!column.isId() && !column.isItemOrder()) {
                String type = column.getJavaType();
                String name = column.getJavaName();
                switch (type) {
                    case "long":
                        resetFunction.addContent(new Assignment(name, "0"));
                        break;
                    case "Boolean":
                        resetFunction.addContent(new Assignment(name, "null"));
                        break;
                    case "Integer":
                    case "Long":
                    case "Date":
                    case "Time":
                    case "Timestamp":
                    case "Money":
                        resetFunction.addContent(new Assignment(name, "null"));
                        resetFunction.addContent(new Assignment(name + "Str", EMPTY_STRING));
                        break;
                    case "String":
                        resetFunction.addContent(new Assignment(name, EMPTY_STRING));
                        break;
                    default:
                        throw new AssertionError("Unknown type: " + type);
                }
            }
        }

        if (!labels.isEmpty()) {
            resetFunction.addContent(EMPTY_LINE);
            for (Column label: labels)
                resetFunction.addContent(new FunctionCall("clearCache", uncapitalize(chopID(label.getJavaName()))).byItself());
        }

        resetFunction
                .addContent(EMPTY_LINE)
                .addContent(new FunctionCall("clearErrorMessages", "dbBeanLocalization").byItself());

        javaClass.addContent(resetFunction).addContent(EMPTY_LINE);

        if (columns.hasItemOrder() || !labels.isEmpty()) {
            var fullResetFunction = new FunctionDeclaration("fullReset")
                    .annotate("@Override")
                    .visibility(Visibility.PUBLIC)
                    .addContent(new FunctionCall("fullReset", "super").byItself());

            if (columns.hasItemOrder())
                fullResetFunction.addContent(new Assignment("itemOrder", "0"));

            if (!labels.isEmpty()) {
                fullResetFunction.addContent(EMPTY_LINE);
                for (Column label: labels)
                    fullResetFunction.addContent(
                            new FunctionCall("fullReset", uncapitalize(chopID(label.getJavaName())))
                                    .byItself());
            }

            javaClass.addContent(fullResetFunction).addContent(EMPTY_LINE);
        }
    }

    private void addDatabaseFunctions() {
        addDBUpdateInnerClasses();
        addCreateRecordFunction();
        addUpdateRecordFunction();
        addUpdateLabelsFunction();
        addTransactionGetter();
    }

    private void addDBUpdateInnerClasses() {
        var function = getOverrideSetupStatementFunction();

        int index = 0;
        for (Column column: columns.getList()) {
            if (!column.isId()) {
                String name = column.getJavaName();
                String type = column.getJavaType();
                ++index;
                if (column.isLabelReference() || column.isFileReference() || column.isBeanReference())
                    function.addContent(getFieldDBUpdateDBUtilFunction("ID", name, index));
                else if (column.isItemOrder())
                    function.addContent(getFieldDBUpdateStatFunction("Long", name, index));
                else if (type.equals("Boolean") || type.equals("Long") || type.equals("Money"))
                    function.addContent(getFieldDBUpdateDBUtilFunction(type, name, index));
                else if (type.equals("Integer"))
                    function.addContent(getFieldDBUpdateDBUtilFunction("Int", name, index));
                else // * String, Date, Time, Timestamp
                    function.addContent(getFieldDBUpdateStatFunction(type, name, index));
            }
        }

        javaClass
                .addContent(new JavaClass("RecordCreationSetup")
                        .visibility(Visibility.PRIVATE)
                        .implementsInterface("DBQuerySetup")
                        .addContent(function))
                .addContent(EMPTY_LINE)
                .addContent(new JavaClass("RecordUpdateSetup")
                        .visibility(Visibility.PRIVATE)
                        .extendsClass("RecordCreationSetup")
                        .addContent(getOverrideSetupStatementFunction()
                                .addContent(new FunctionCall("setupPreparedStatement", "super")
                                        .byItself()
                                        .addArgument("stat"))
                                .addContent(getFieldDBUpdateStatFunction("Long", "id", index + 1))))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getOverrideSetupStatementFunction() {
        return new FunctionDeclaration("setupPreparedStatement")
                .annotate("@Override")
                .visibility(Visibility.PUBLIC)
                .addException("SQLException")
                .addArgument(new FunctionArgument("PreparedStatement", "stat"));
    }

    private FunctionCall getFieldDBUpdateDBUtilFunction(String type, String fieldName, int index) {
        return new FunctionCall("set" + type, "DBUtil")
                .byItself()
                .addArguments("stat", Integer.toString(index), fieldName);
    }

    private FunctionCall getFieldDBUpdateStatFunction(String type, String fieldName, int index) {
        return new FunctionCall("set" + type, "stat")
                .byItself()
                .addArguments(Integer.toString(index), fieldName);
    }

    private void addCreateRecordFunction() {
        var function = getDBUpdateFunction("createRecord", true)
                .addContent(new FunctionCall("preCreateExtraDbActions").byItself().addArgument("transaction"));

        var labels = columns.getLabels();
        for (Column label: labels)
            function.addContent(getUpdateLabelFunctionCall(label));

        columns.getItemOrderColumn().ifPresent(column -> {
            if (Strings.isEmpty(column.getItemOrderAssociatedField()))
                function
                        .addContent(new IfBlock(new Condition("itemOrder == 0"))
                                .addContent(new Assignment(
                                        "itemOrder",
                                        getItemOrderPlusOneExpression(getMaxItemOrderFunctionCallStart()
                                                .addArgument(new FunctionCall("getItemOrderMaxQuery", "dbBeanItemOrderManager"))))));
            else {
                String secondaryField = getItemOrderSecondaryFieldJavaName(column);
                function
                        .addContent(new IfBlock(new Condition("itemOrder == 0"))
                                .addContent(new IfBlock(new Condition(secondaryField + " == 0"))
                                        .addContent(new Assignment(
                                                "itemOrder",
                                                getItemOrderPlusOneExpression(getMaxItemOrderFunctionCallStart()
                                                        .addArgument(new FunctionCall(
                                                                "getItemOrderMaxQueryWithNullSecondaryField",
                                                                "dbBeanItemOrderManager")))))
                                        .elseClause(new ElseBlock()
                                                .addContent(new Assignment(
                                                        "itemOrder",
                                                        getItemOrderPlusOneExpression(getMaxItemOrderFunctionCallStart()
                                                                .addArgument(new FunctionCall(
                                                                        "getItemOrderMaxQuery",
                                                                        "dbBeanItemOrderManager"))
                                                                .addArgument(secondaryField)))))));
            }
        });

        function.addContent(new VarDeclaration(
                "long",
                "id",
                new FunctionCall("addRecordCreation", "transaction")
                        .addArgument(quickQuote(getBeanCreationQuery()))
                        .addArgument(new ObjectCreation("RecordCreationSetup"))));

        if (!labels.isEmpty())
            function.addContent(new FunctionCall("updateLabels").byItself().addArgument("transaction"));

        function.addContent(new FunctionCall("createExtraDbActions").byItself().addArguments("transaction", "id"))
                .addContent(new ReturnStatement("id"));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private void addUpdateRecordFunction() {
        var function = getDBUpdateFunction("updateRecord", false)
                .addContent(new FunctionCall("preUpdateExtraDbActions").byItself().addArgument("transaction"));

        var labels = columns.getLabels();
        for (Column label: labels)
            function.addContent(new FunctionCall("init" + chopID(label.getJavaName())).byItself());
        for (Column label: labels)
            function.addContent(getUpdateLabelFunctionCall(label));

        function.addContent(new FunctionCall("addUpdate", "transaction")
                .byItself()
                .addArgument(quickQuote(getBeanUpdateQuery()))
                .addArgument(new ObjectCreation("RecordUpdateSetup")));

        if (!labels.isEmpty())
            function.addContent(new FunctionCall("updateLabels").byItself().addArgument("transaction"));

        function.addContent(new FunctionCall("updateExtraDbActions").byItself().addArgument("transaction"));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private void addUpdateLabelsFunction() {
        var labels = columns.getLabels();
        if (labels.isEmpty())
            return;

        var function = new FunctionDeclaration("updateLabels")
                .visibility(Visibility.PRIVATE)
                .addArgument(new FunctionArgument("DBTransaction", "transaction"));

        for (Column label: labels)
            function.addContent(new FunctionCall("commitTextsToDatabase", uncapitalize(chopID(label.getJavaName())))
                    .byItself()
                    .addArgument("transaction"));

        javaClass.addContent(function).addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getDBUpdateFunction(String functionName, boolean returnID) {
        FunctionDeclaration function;
        if (returnID)
            function = new FunctionDeclaration(functionName, "long");
        else
            function = new FunctionDeclaration(functionName);

        return function
                .annotate("@Override")
                .visibility(Visibility.PROTECTED)
                .addArgument(new FunctionArgument("DBTransaction", "transaction"));
    }

    private FunctionCall getUpdateLabelFunctionCall(Column column) {
        String name = column.getJavaName();

        return new FunctionCall("set" + capitalize(name))
                .byItself()
                .addArgument(new FunctionCall("updateDB", uncapitalize(chopID(name)))
                        .addArgument("transaction"));
    }

    private OperatorExpression getItemOrderPlusOneExpression(FunctionCall functionCall) {
        return new OperatorExpression(
                functionCall,
                "1",
                OperatorExpression.Operator.ADD);
    }

    private FunctionCall getMaxItemOrderFunctionCallStart() {
        return new FunctionCall("getMaxItemOrder", "dbBeanItemOrderManager")
                .addArgument("transaction");
    }

    private String getBeanCreationQuery() {
        StringBuilder buf = new StringBuilder();

        buf.append("INSERT INTO ");
        buf.append(tableName);
        buf.append(" (");

        int count = 0;
        for (Column column: columns.getList()) {
            final String name = column.getSqlName();
            if (!name.equals("id")) {
                count++;
                buf.append(backquote(name));
                buf.append(", ");
            }
        }
        buf.delete(buf.length() - 2, buf.length());

        buf.append(") VALUES (");

        buf.append("?, ".repeat(count));
        buf.delete(buf.length() - 2, buf.length());

        buf.append(")");

        return buf.toString();
    }

    private String getBeanUpdateQuery() {
        StringBuilder buf = new StringBuilder();

        buf.append("UPDATE ");
        buf.append(tableName);
        buf.append(" SET ");

        for (Column column: columns.getList()) {
            final String name = column.getSqlName();
            if (!name.equals("id")) {
                buf.append(backquote(name));
                buf.append("=?, ");
            }
        }
        buf.delete(buf.length() - 2, buf.length());

        buf.append(" WHERE id=?");

        if (columns.hasLastUpdate())
            buf.append(" AND last_update=?");

        return buf.toString();
    }

    private String backquote(String fieldName) {
        return "`" + fieldName + "`";
    }

    private void addTransactionGetter() {
        javaClass
                .addContent(new FunctionDeclaration("createDBTransaction", "DBTransaction")
                        .annotate("@Override")
                        .visibility(Visibility.PROTECTED)
                        .addContent(new ReturnStatement(new FunctionCall("createDBTransaction", "DbBeans"))))
                .addContent(EMPTY_LINE);
    }

}
