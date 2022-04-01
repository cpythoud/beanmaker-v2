package org.beanmaker.v2.codegen;

import org.jcodegen.java.AnonymousClassCreation;
import org.jcodegen.java.Condition;
import org.jcodegen.java.ForEach;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.IfBlock;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;

public class LabelManagerSourceFile extends BaseCode {

    private static final List<String> JAVA_UTIL_IMPORTS =
            createImportList("java.util", "List", "Optional");
    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DbBeanLabel", "DbBeanLabelBasicFunctions",
                    "DbBeanLanguage", "MissingImplementationException");

    private static final FunctionArgument ID_ARG = new FunctionArgument("long", "id");
    private static final FunctionArgument NAME_ARG = new FunctionArgument("String", "name");
    private static final FunctionArgument TRANSACTION_ARG = new FunctionArgument("DBTransaction", "transaction");
    private static final FunctionArgument LANG_ARG = new FunctionArgument("DbBeanLanguage", "dbBeanLanguage");
    private static final FunctionArgument PARAMETERS_ARG = new FunctionArgument("Object...", "parameters");
    private static final FunctionArgument LABEL_ARG = new FunctionArgument("DbBeanLabel", "dbBeanLabel");

    public LabelManagerSourceFile(String packageName) {
        super("LabelManager", packageName);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        addImports(JAVA_UTIL_IMPORTS, BM_RUNTIME_IMPORTS);
        importsManager.addImport("org.dbbeans.sql.DBTransaction");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC);
    }

    @Override
    protected void addCoreFunctionality() {
        addNonImplementedStaticFunction("DbBeanLabel", "get", ID_ARG);
        addNonImplementedStaticFunction("DbBeanLabel", "get", NAME_ARG);
        addNonImplementedStaticFunction("boolean", "isIdOK", ID_ARG);
        addNonImplementedStaticFunction("boolean", "isIdOK", ID_ARG, TRANSACTION_ARG);
        addNonImplementedStaticFunction("boolean", "isNameOK", NAME_ARG);
        addNonImplementedStaticFunction("boolean", "isNameOK", NAME_ARG, TRANSACTION_ARG);
        addNonImplementedStaticFunction("String", "get", ID_ARG, LANG_ARG);
        addNonImplementedStaticFunction("String", "get", ID_ARG, LANG_ARG, PARAMETERS_ARG);
        addNonImplementedStaticFunction("String", "get", NAME_ARG, LANG_ARG);
        addNonImplementedStaticFunction("String", "get", NAME_ARG, LANG_ARG, PARAMETERS_ARG);

        addNonImplementedStaticFunction("DbBeanLabel", "createInstance");
        addNonImplementedStaticFunction("DbBeanLabel", "duplicate", LABEL_ARG);
        addNonImplementedStaticFunction("DbBeanLanguage", "getDefaultLanguage");
        addNonImplementedStaticFunction("List<DbBeanLanguage>", "getAllActiveLanguages");
        addNonImplementedStaticFunction("DbBeanLanguage", "getLanguage", ID_ARG);

        addReplaceDataFunction();
        addFunctionsWithOptionalResults();
        addBasicFunctionsClass();
    }

    private void addReplaceDataFunction() {
        javaClass
                .addContent(new FunctionDeclaration("replaceData", "DbBeanLabel")
                        .visibility(Visibility.PUBLIC)
                        .markAsStatic()
                        .addArgument(new FunctionArgument("DbBeanLabel", "into"))
                        .addArgument(new FunctionArgument("DbBeanLabel", "from"))
                        .addContent(new FunctionCall("clearCache", "into")
                                .byItself())
                        .addContent(new ForEach("DbBeanLanguage", "dbBeanLanguage", new FunctionCall("getAllActiveLanguages"))
                                .addContent(new IfBlock(new Condition(new FunctionCall("hasDataFor", "from").addArgument("dbBeanLanguage")))
                                        .addContent(new FunctionCall("updateLater", "into")
                                                .byItself()
                                                .addArgument("dbBeanLanguage")
                                                .addArgument(new FunctionCall("get", "from").addArgument("dbBeanLanguage")))))
                        .addContent(EMPTY_LINE)
                        .addContent(new ReturnStatement("into")))
                .addContent(EMPTY_LINE);
    }

    private void addFunctionsWithOptionalResults() {
        javaClass
                .addContent(getOptionalResultFunction("long", "id", "isIdOK"))
                .addContent(EMPTY_LINE)
                .addContent(getOptionalResultFunction("String", "name", "isNameOK"))
                .addContent(EMPTY_LINE);
    }

    private FunctionDeclaration getOptionalResultFunction(String argType, String argName, String validationFunction) {
        return new FunctionDeclaration("getPossibleLabel", "Optional<DbBeanLabel>")
                .visibility(Visibility.PUBLIC)
                .markAsStatic()
                .addArgument(new FunctionArgument(argType, argName))
                .addContent(new IfBlock(new Condition(new FunctionCall(validationFunction).addArgument(argName)))
                        .addContent(new ReturnStatement(new FunctionCall("of", "Optional")
                                .addArgument(new FunctionCall("get").addArgument(argName)))))
                .addContent(EMPTY_LINE)
                .addContent(new ReturnStatement(new FunctionCall("empty", "Optional")));
    }

    private void addBasicFunctionsClass() {
        var anonymousClass = new AnonymousClassCreation("DbBeanLabelBasicFunctions")
                .addContent(new FunctionDeclaration("getPossibleLabel", "Optional<DbBeanLabel>")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("String", "name"))
                        .addContent(new ReturnStatement(new FunctionCall("getPossibleLabel", "LabelManager")
                                .addArgument("name"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("isNameOK", "boolean")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument("String", "name"))
                        .addContent(new ReturnStatement(new FunctionCall("isNameOK", "LabelManager")
                                .addArgument("name"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("getDefaultLanguage", "DbBeanLanguage")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("getDefaultLanguage", "LabelManager"))));

        var declaration = new VarDeclaration("DbBeanLabelBasicFunctions", "basicFunctions", anonymousClass)
                .visibility(Visibility.PRIVATE)
                .markAsStatic()
                .markAsFinal();
        anonymousClass.setIndentationLevel(1);

        javaClass.addContent(declaration).addContent(EMPTY_LINE);
        addObjectRetrievalFunction();
    }

    private void addObjectRetrievalFunction() {
        javaClass
                .addContent(new FunctionDeclaration("getBasicFunctions", "DbBeanLabelBasicFunctions")
                        .visibility(Visibility.PUBLIC)
                        .markAsStatic()
                        .addContent(new ReturnStatement("basicFunctions")))
                .addContent(EMPTY_LINE);
    }

}
