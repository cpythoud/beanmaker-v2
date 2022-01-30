package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.java.ClassSourceFile;
import org.jcodegen.java.EmptyLine;
import org.jcodegen.java.ExceptionThrow;
import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ImportsManager;
import org.jcodegen.java.JavaClass;
import org.jcodegen.java.Visibility;

import java.util.ArrayList;
import java.util.List;

import static org.beanmaker.v2.util.Strings.capitalize;

public abstract class BaseCode implements BeanMakerSourceFile {

    protected static final ProjectParameters DEFAULT_PROJECT_PARAMETERS = new DefaultProjectParameters();

    protected static final EmptyLine EMPTY_LINE = new EmptyLine();
    protected static final String EMPTY_STRING = "\"\"";

    protected final ClassSourceFile sourceFile;
    protected final JavaClass javaClass;
    protected final ImportsManager importsManager;

    protected final String className;
    protected final ProjectParameters projectParameters;

    public BaseCode(String className, String packageName) {
        this(className, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BaseCode(String className, String packageName, ProjectParameters projectParameters) {
        if (Strings.isEmpty(className))
            throw new IllegalArgumentException("className empty");
        if (Strings.isEmpty(packageName))
            throw new IllegalArgumentException("packageName empty");

        sourceFile = new ClassSourceFile(packageName, className);
        javaClass = sourceFile.getJavaClass();
        javaClass.addContent(EMPTY_LINE);
        importsManager = sourceFile.getImportsManager();

        this.className = className;
        this.projectParameters = projectParameters;
    }

    @Override
    public String getSourceCode() {
        return sourceFile.toString();
    }

    @Override
    public String getFilename() {
        return sourceFile.getFilename();
    }


    protected void createSourceCode() {
        sourceFile.setStartComment(SourceFiles.getCommentAndVersion());

        addImports();
        decorateJavaClass();
        addStaticProperties();
        addStaticInitialization();
        addProperties();
        addConstructors();
        addCoreFunctionality();
    }

    protected void addImports() { }

    protected void decorateJavaClass() { }

    protected void addStaticProperties() { }

    protected void addStaticInitialization() { }

    protected void addProperties() { }

    protected void addConstructors() { }

    protected abstract void addCoreFunctionality();


    protected void newLine() {
        javaClass.addContent(EMPTY_LINE);
    }

    @SafeVarargs
    protected final void addImports(List<String>... importLists) {
        for (var importList: importLists)
            for (String importname : importList)
                importsManager.addImport(importname);
    }

    protected static List<String> createImportList(String packageName, String... classes) {
        var importList = new ArrayList<String>();
        for (String className: classes)
            importList.add(packageName + "." + className);
        return importList;
    }

    protected String getIsEmptyFunctionName(Column column) {
        return "is" + capitalize(column.getJavaName()) + "Empty";
    }

    /*protected void addProperty(VarDeclaration property) {
        javaClass.addContent(property.visibility(Visibility.PRIVATE));
    }

    protected void addProperty(String type, String var) {
        addProperty(new VarDeclaration(type, var));
    }

    protected void addProperty(String type, String var, String val) {
        addProperty(new VarDeclaration(type, var, val));
    }

    protected void addInheritableProperty(VarDeclaration property) {
        javaClass.addContent(property.visibility(Visibility.PROTECTED));
    }

    protected void addInheritableProperty(String type, String var) {
        addInheritableProperty(new VarDeclaration(type, var));
    }

    protected void addInheritableProperty(String type, String var, String val) {
        addInheritableProperty(new VarDeclaration(type, var, val));
    }

    protected void addInheritableProperty(String type, String var, ObjectCreation object) {
        addInheritableProperty(new VarDeclaration(type, var, object));
    }*/

    protected String getVarNameForClass(String className) {
        String[] parts = className.split("\\.");
        return Strings.uncapitalize(parts[parts.length - 1]);
    }

    protected void addNonImplementedFunction(String name, FunctionArgument... functionArguments) {
        addNonImplementedFunction(null, name, false, false, Visibility.PUBLIC, functionArguments);
    }

    protected void addNonImplementedOverriddenFunction(
            String returnType,
            String name,
            Visibility visibility,
            FunctionArgument... functionArguments)
    {
        addNonImplementedFunction(returnType, name, false, true, visibility, functionArguments);
    }

    protected void addNonImplementedStaticFunction(
            String returnType,
            String name,
            FunctionArgument... functionArguments)
    {
        addNonImplementedFunction(returnType, name, true, false, Visibility.PUBLIC, functionArguments);
    }

    private void addNonImplementedFunction(
            String returnType,
            String name,
            boolean staticFunction,
            boolean overridden,
            Visibility visibility,
            FunctionArgument... functionArguments)
    {
        FunctionDeclaration functionDeclaration;
        if (returnType == null)
            functionDeclaration = new FunctionDeclaration(name);
        else
            functionDeclaration = new FunctionDeclaration(name, returnType);

        functionDeclaration.visibility(visibility);

        if (staticFunction)
            functionDeclaration.markAsStatic();

        if (overridden)
            functionDeclaration.annotate("@Override");

        StringBuilder argTypeList = new StringBuilder();
        for (FunctionArgument argument: functionArguments) {
            functionDeclaration.addArgument(argument);
            argTypeList.append(argument.getType()).append(", ");
        }
        if (argTypeList.length() > 0)
            argTypeList.delete(argTypeList.length() - 2, argTypeList.length());

        functionDeclaration.addContent(
                new ExceptionThrow("MissingImplementationException")
                        .addArgument(Strings.quickQuote(
                                className + "." + name + "(" + argTypeList.toString() + ")"))
        );

        javaClass.addContent(functionDeclaration).addContent(EMPTY_LINE);
    }

}
