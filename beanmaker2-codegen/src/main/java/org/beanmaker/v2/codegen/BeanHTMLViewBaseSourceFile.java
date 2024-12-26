package org.beanmaker.v2.codegen;

import org.jcodegen.java.Assignment;
import org.jcodegen.java.Comparison;
import org.jcodegen.java.Condition;
import org.jcodegen.java.ElseBlock;
import org.jcodegen.java.ElseIfBlock;
import org.jcodegen.java.ForEach;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;

import static org.beanmaker.v2.util.Strings.capitalize;
import static org.beanmaker.v2.util.Strings.quickQuote;
import static org.beanmaker.v2.util.Strings.uncapitalize;

public class BeanHTMLViewBaseSourceFile extends BeanCodeWithDBInfo {

    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DbBeanLanguage", "HFHParameters", "HttpRequestParameters");
    private static final List<String> HTML_IMPORTS =
            createImportList("org.jcodegen.html", "FormTag", "Tag");

    private static final int TEXTAREA_THRESHOLD = 1000;

    private final String editorClass;
    private final String editorObject;

    public BeanHTMLViewBaseSourceFile(String beanName, String packageName, Columns columns) {
        this(beanName, packageName, columns, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanHTMLViewBaseSourceFile(String beanName, String packageName, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, null, "HTMLViewBase", columns, projectParameters);

        editorClass = beanName + "Editor";
        editorObject = uncapitalize(editorClass);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(BM_RUNTIME_IMPORTS, HTML_IMPORTS);
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.markAsAbstract().extendsClass("HTMLView");
        applySealedModifier(beanName + "HTMLView");
    }

    @Override
    protected void addStaticProperties() {
        if (columns.hasFiles())
            declareFileCreator();

        javaClass
                .addContent(new VarDeclaration(editorClass, editorObject).markAsFinal())
                .addContent(EMPTY_LINE);
    }

    private void declareFileCreator() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanFileCreator");

        javaClass
                .addContent(new VarDeclaration(
                        "DbBeanFileCreator",
                        "dbBeanFileCreator",
                        new ObjectCreation("DbBeanFileCreator")
                                .addArgument(getFileManagerData("getDefaultUploadDir"))
                                .addArgument(getFileManagerData("getInternalFileNameCalculator"))
                                .addArgument(getFileManagerData("getSubDirFileCountThreshold")))
                        .markAsFinal())
                .addContent(EMPTY_LINE);
    }

    private FunctionCall getFileManagerData(String function) {
        return new FunctionCall(function, "LocalFileManager");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument(editorClass, editorObject))
                        .addArgument(getLanguageArgument())
                        .addContent(new FunctionCall("super")
                                .byItself()
                                .addArgument(editorObject)
                                .addArgument(new FunctionCall("getLocalization", beanName + "Parameters.INSTANCE")
                                        .addArgument("dbBeanLanguage")))
                        .addContent(new FunctionCall("setCurrentDbBeanLanguage", editorObject)
                                .byItself()
                                .addArgument("dbBeanLanguage"))
                        .addContent(new Assignment("this." + editorObject, editorObject)))
                .addContent(EMPTY_LINE);
    }

    private FunctionArgument getLanguageArgument() {
        return new FunctionArgument("DbBeanLanguage", "dbBeanLanguage");
    }

    @Override
    protected void addCoreFunctionality() {
        addEditorGetter();
        addRequiredChecks();
        addFormGetter();
        addFormFieldComposers();
        addAllFieldsSetter();
    }

    private void addEditorGetter() {
        javaClass
                .addContent(new FunctionDeclaration("get" + editorClass, editorClass)
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(editorObject)))
                .addContent(EMPTY_LINE);
    }

    private void addRequiredChecks() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addRequiredCheck(column);
    }

    private void addRequiredCheck(Column column) {
        String name = capitalize(column.getJavaName());

        javaClass
                .addContent(getRequiredFieldFunctionDeclaration(name)
                        .addContent(new ReturnStatement(getRequiredFunctionCall(name))))
                .addContent(EMPTY_LINE);

        if (column.isLabelReference())
            javaClass
                    .addContent(getRequiredFieldFunctionDeclaration(name)
                            .addArgument(getLanguageArgument())
                            .addContent(new ReturnStatement(getRequiredFunctionCall(name)
                                    .addArgument("dbBeanLanguage"))))
                    .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getRequiredFieldFunctionDeclaration(String fieldName) {
        return new FunctionDeclaration("is" + fieldName + "RequiredInHtmlForm", "boolean");
    }

    private FunctionCall getRequiredFunctionCall(String fieldName) {
        return new FunctionCall("is" + fieldName + "Required", editorObject);
    }

    private void addFormGetter() {
        var formTagFunction = new FunctionDeclaration("getHtmlFormTag", "FormTag")
                .annotate("@Override")
                .visibility(Visibility.PUBLIC)
                .addContent(new VarDeclaration("FormTag", "form", new FunctionCall("getFormStart")));

        if (columns.hasFiles())
            formTagFunction.addContent(new FunctionCall("enctype", "form")
                    .byItself()
                    .addArgument("FormTag.EncodingType.MULTIPART"));

        formTagFunction.addContent(new FunctionCall("composeHiddenSubmitField").byItself().addArgument("form"));

        for (Column column: columns.getList())
            if (!column.isSpecial())
                formTagFunction.addContent(getComposeFunctionCall(column));

        formTagFunction
                .addContent(new FunctionCall("composeAdditionalHtmlFormFields").byItself().addArgument("form"))
                .addContent(new IfBlock(new Condition("!readonly"))
                        .addContent(new FunctionCall("composeButtons").byItself().addArgument("form")))
                .addContent(EMPTY_LINE)
                .addContent(new ReturnStatement("form"));

        javaClass.addContent(formTagFunction).addContent(EMPTY_LINE);
    }

    private FunctionCall getComposeFunctionCall(Column column) {
        return new FunctionCall("compose" + capitalize(column.getJavaName()) + "FormElement")
                .byItself()
                .addArgument("form");
    }

    private void addFormFieldComposers() {
        for (Column column: columns.getList())
            if (!column.isSpecial())
                addFormField(column);
    }

    private void addFormField(Column column) {
        String type = column.getJavaType();
        String name = column.getJavaName();
        String capName = capitalize(name);

        if (column.isBeanReference() && !column.isFileReference() && !column.isLabelReference()) {
            importsManager.addImport("java.util.List");
            importsManager.addImport("org.beanmaker.v2.runtime.IdNamePair");

            javaClass
                    .addContent(new FunctionDeclaration("get" + capName + "FormElementIdNamePairs", "List<IdNamePair>")
                            .addContent(new ReturnStatement(new FunctionCall("getPairs", "IdNamePair")
                                    .addArgument(new FunctionCall("getAll", column.getAssociatedBeanClass()))
                                    .addArgument("dbBeanLocalization")
                                    .addArgument(quickQuote(name + "_please_select")))))
                    .addContent(EMPTY_LINE);
        }

        var parametersFunction =
                new FunctionDeclaration("get" + capName + "FormElementParameters", "HFHParameters")
                        .addContent(VarDeclaration.declareAndInit("HFHParameters", "params"))
                        .addContent(getParamSetterExpression("Field", quickQuote(name)))
                        .addContent(getParamSetterExpression("IdBean", new FunctionCall("getId", editorObject)));

        if (type.equals("String"))
            parametersFunction.addContent(getParamSetterExpressionFromEditor("Value", capName));
        else if (type.equals("Integer") || type.equals("Long") || TEMPORAL_TYPES.contains(type) || type.equals("Money"))
            parametersFunction.addContent(getParamSetterExpressionFromEditor("Value", capName + "Str"));
        else if (type.equals("Boolean"))
            parametersFunction.addContent(getParamSetterExpression(
                    new Condition(new FunctionCall("is" + capName + "Empty", "!" + editorObject))
                            .andCondition(new Condition(new FunctionCall("is" + capName, editorObject)))));
        else if (column.isBeanReference() && !column.isFileReference() && !column.isLabelReference())
            parametersFunction.addContent(getParamSetterExpressionFromEditor("Selected", capName));

        if (column.isFileReference())
            parametersFunction.addContent(getParamSetterExpression(
                    "CurrentFile",
                    new FunctionCall("getFilename", "LocalFileManager")
                            .addArgument(new FunctionCall("getIdFile", editorObject))));

        parametersFunction.addContent(getParamSetterExpressionFromEditor("FieldLabel", capName + "Label"));

        switch (type) {
            case "Integer":
            case "Long":
                parametersFunction.addContent(getFieldInputType("NUMBER"));
                break;
            case "String":
                if (column.getDisplaySize() < TEXTAREA_THRESHOLD) {
                    if (name.equals("email") || name.endsWith("Email"))
                        parametersFunction.addContent(getFieldInputType("EMAIL"));
                    else
                        parametersFunction.addContent(getFieldInputType("TEXT"));
                }
                break;
            case "Date":
                parametersFunction.addContent(getFieldInputType("DATE"));
                break;
            case "Time":
                parametersFunction.addContent(getFieldInputType("TIME"));
                break;
            case "Timestamp":
                parametersFunction.addContent(getFieldInputType("DATETIME"));
                break;
            case "Money":
                parametersFunction.addContent(getFieldInputType("TEXT"));
                break;
            default:
                if (column.isFileReference())
                    parametersFunction.addContent(getFieldInputType("FILE"));
        }

        if (column.isBeanReference() && !column.isFileReference() && !column.isLabelReference())
            parametersFunction.addContent(getParamSetterExpression(
                    "SelectPairs",
                    new FunctionCall("get" + capName + "FormElementIdNamePairs")));

        parametersFunction
                .addContent(getParamSetterExpression("Required", new FunctionCall("is" + capName + "RequiredInHtmlForm")))
                .addContent(getParamSetterExpression("Readonly", "readonly"));

        if (type.equals("String"))
            parametersFunction.addContent(getParamSetterExpression("MaxLength", Integer.toString(column.getDisplaySize())));

        parametersFunction.addContent(new ReturnStatement("params"));

        javaClass.addContent(parametersFunction).addContent(EMPTY_LINE);

        var composeFunction = new FunctionDeclaration("compose" + capName + "FormElement")
                .addArgument(new FunctionArgument("Tag", "form"));

        if (column.isLabelReference())
            composeFunction.addContent(
                    new ForEach(
                            "DbBeanLanguage",
                            "dbBeanLanguage",
                            new FunctionCall("getAllActiveLanguages", "LabelManager"))
                            .addContent(new FunctionCall("child", "form")
                                    .byItself()
                                    .addArgument(new FunctionCall("getLabelFormField", "htmlFormHelper")
                                            .addArgument(new FunctionCall("get" + chopID(name), editorObject).addArgument("dbBeanLanguage"))
                                            .addArgument("dbBeanLanguage")
                                            .addArgument(new FunctionCall("is" + capName + "RequiredInHtmlForm").addArgument("dbBeanLanguage"))
                                            .addArgument(new FunctionCall("get" + capName + "FormElementParameters")))));
        else if (column.isFileReference())
            composeFunction.addContent(getStandardFieldComposeFunction("File", capName));
        else if (column.isBeanReference())
            composeFunction.addContent(getStandardFieldComposeFunction("Select", capName));
        else if (type.equals("Boolean"))
            composeFunction.addContent(getStandardFieldComposeFunction("Checkbox", capName));
        else if (type.equals("String") && column.getDisplaySize() >= TEXTAREA_THRESHOLD)
            composeFunction.addContent(getStandardFieldComposeFunction("TextArea", capName));
        else
            composeFunction.addContent(getStandardFieldComposeFunction("Text", capName));

        javaClass.addContent(composeFunction).addContent(EMPTY_LINE);
    }

    private FunctionCall getParamSetterExpression(String paramName, String value) {
        return new FunctionCall("set" + paramName, "params").byItself().addArgument(value);
    }

    private FunctionCall getParamSetterExpression(String paramName, FunctionCall value) {
        return new FunctionCall("set" + paramName, "params").byItself().addArgument(value);
    }

    private FunctionCall getParamSetterExpression(Condition value) {
        return new FunctionCall("setChecked", "params").byItself().addArgument(value);
    }

    private FunctionCall getParamSetterExpressionFromEditor(String paramName, String fieldReference) {
        return getParamSetterExpression(paramName, new FunctionCall("get" + fieldReference, editorObject));
    }

    private FunctionCall getFieldInputType(String type) {
        importsManager.addImport("org.jcodegen.html.InputTag");
        return getParamSetterExpression("InputType", "InputTag.InputType." + type);
    }

    private FunctionCall getStandardFieldComposeFunction(String fieldType, String fieldName) {
        return new FunctionCall("child", "form")
                .byItself()
                .addArgument(new FunctionCall("get" + fieldType + "Field", "htmlFormHelper")
                        .addArgument(new FunctionCall("get" + fieldName + "FormElementParameters")));
    }

    private void addAllFieldsSetter() {
        var allFieldsSetterFunction = new FunctionDeclaration("setAllFields")
                .annotate("@Override")
                .visibility(Visibility.PUBLIC)
                .addArgument(new FunctionArgument("HttpRequestParameters", "parameters"));

        if (columns.hasFiles()) {
            for (Column column: columns.getList()) {
                if (column.isFileReference()) {
                    String name = column.getJavaName();
                    allFieldsSetterFunction.addContent(new VarDeclaration(
                            "long",
                            name,
                            new FunctionCall("get" + capitalize(name), editorObject)));
                }
            }
            allFieldsSetterFunction.addContent(EMPTY_LINE);
        }

        allFieldsSetterFunction.addContent(new FunctionCall("reset").byItself()).addContent(EMPTY_LINE);

        for (Column column: columns.getList()) {
            if (!column.isSpecial()) {
                String name = column.getJavaName();
                String capName = capitalize(name);
                String choppedIdName = name.substring(2);
                if (column.isLabelReference())
                    allFieldsSetterFunction.addContent(new ForEach(
                            "DbBeanLanguage",
                            "dbBeanLanguage",
                            new FunctionCall("getAllActiveLanguages", "LabelManager"))
                            .addContent(new VarDeclaration("String", "iso", new FunctionCall("getTag", "dbBeanLanguage")))
                            .addContent(new FunctionCall("set" + choppedIdName, editorObject)
                                    .byItself()
                                    .addArgument("dbBeanLanguage")
                                    .addArgument(new FunctionCall("getValue", "parameters")
                                            .addArgument(quickQuote(name) + " + iso"))));
                else if (column.isFileReference())
                    allFieldsSetterFunction
                            .addContent(
                                    new IfBlock(new Condition(new FunctionCall("hasUploadedFile", "parameters").addArgument(quickQuote(name))))
                                            .addContent(new FunctionCall("set" + choppedIdName, editorObject)
                                                    .byItself()
                                                    .addArgument(new FunctionCall("create", "dbBeanFileCreator")
                                                            .addArgument(new FunctionCall("getOrCreateEditor", "LocalFileManager")
                                                                    .addArgument(new FunctionCall("get" + capName, editorObject)))
                                                            .addArgument(new FunctionCall("getUploadedFile", "parameters")
                                                                    .addArgument(quickQuote(name))))))
                            .addContent(
                                    new IfBlock(new Condition(new FunctionCall("hasParameter", "parameters")
                                            .addArgument(quickQuote("delete_" + name))))
                                            .addContent(new FunctionCall("set" + capName, editorObject)
                                                    .byItself()
                                                    .addArgument("0"))
                                            .addElseIfClause(new ElseIfBlock(new Condition(new Comparison(
                                                    new FunctionCall("get" + capName, editorObject),
                                                    "0",
                                                    Comparison.Comparator.EQUAL)))
                                                    .addContent(new FunctionCall("set" + capName, editorObject)
                                                            .byItself()
                                                            .addArgument("idFile"))));
                else if (column.isBeanReference()) {
                    importsManager.addImport("org.beanmaker.v2.util.Strings");
                    String nameParamStr = name + "ParamStr";
                    allFieldsSetterFunction
                            .addContent(new VarDeclaration(
                                    "String",
                                    nameParamStr,
                                    new FunctionCall("getValue", "parameters")
                                            .addArgument(quickQuote(name))))
                            .addContent(new IfBlock(new Condition(new Comparison(nameParamStr, "null", Comparison.Comparator.NEQ)))
                                    .addContent(new FunctionCall("set" + capName, editorObject)
                                            .byItself()
                                            .addArgument(new FunctionCall("getLongVal", "Strings")
                                                    .addArgument(nameParamStr)))
                                    .elseClause(new ElseBlock().addContent(
                                            new FunctionCall("set" + capName, editorObject)
                                                    .byItself()
                                                    .addArgument("0"))));
                } else {
                    String type = column.getJavaType();
                    switch (type) {
                        case "Boolean":
                            allFieldsSetterFunction.addContent(
                                    new IfBlock(new Condition(new Comparison(
                                            new FunctionCall("getValue", "parameters")
                                                    .addArgument(quickQuote(name)),
                                            "null",
                                            Comparison.Comparator.NEQ)))
                                            .addContent(new FunctionCall("set" + capName, editorObject)
                                                    .byItself()
                                                    .addArgument("true"))
                                            .elseClause(new ElseBlock().addContent(
                                                    new FunctionCall("set" + capName, editorObject)
                                                            .byItself()
                                                            .addArgument("false"))));
                            break;
                        case "Integer":
                        case "Long":
                        case "Date":
                        case "Time":
                        case "Timestamp":
                        case "Money":
                            allFieldsSetterFunction.addContent(
                                    new FunctionCall("set" + capName + "Str", editorObject)
                                            .byItself()
                                            .addArgument(new FunctionCall("getValue", "parameters")
                                                    .addArgument(quickQuote(name))));
                            break;
                        case "String":
                            allFieldsSetterFunction.addContent(
                                    new FunctionCall("set" + capName, editorObject)
                                            .byItself()
                                            .addArgument(new FunctionCall("getValue", "parameters")
                                                    .addArgument(quickQuote(name))));
                            break;
                        default:
                            throw new AssertionError("Unexpected/unknown type: " + type);
                    }
                }
            }
        }

        javaClass.addContent(allFieldsSetterFunction).addContent(EMPTY_LINE);
    }

}
