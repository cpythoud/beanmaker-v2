package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Version;

import org.jcodegen.java.EmptyLine;
import org.jcodegen.java.EnumSourceFile;
import org.jcodegen.java.ImportsManager;
import org.jcodegen.java.JavaEnum;

import java.time.Instant;

public abstract class BaseEnumCode implements BeanMakerSourceFile {

    protected final String enumName;
    protected final String packageName;

    protected final EnumSourceFile sourceFile;
    protected final JavaEnum javaEnum;
    protected final ImportsManager importsManager;
    protected final ProjectParameters projectParameters;

    protected static final EmptyLine EMPTY_LINE = new EmptyLine();

    public BaseEnumCode(String enumName, String packageName, ProjectParameters projectParameters) {
        this.enumName = enumName;
        this.packageName = packageName;
        this.projectParameters = projectParameters;

        sourceFile = new EnumSourceFile(packageName, enumName);
        javaEnum = sourceFile.getJavaEnum();
        importsManager = sourceFile.getImportsManager();
    }

    @Override
    public String getSourceCode() {
        addGeneratedAnnotation();
        return sourceFile.toString();
    }

    @Override
    public String getFilename() {
        return sourceFile.getFilename();
    }

    protected void createSourceCode() {
        sourceFile.setStartComment(SourceFiles.getCommentAndVersion());

        addImports();
        decorateJavaEnum();
        enumerateConstants();
        addStaticProperties();
        addStaticInitialization();
        addProperties();
        addConstructors();
        addStaticFunctions();
        addOptionalFunctionality();
    }

    protected void addImports() { }

    protected void decorateJavaEnum() { }

    protected abstract void enumerateConstants();

    protected void addStaticProperties() { }

    protected void addStaticInitialization() { }

    protected void addProperties() { }

    protected void addConstructors() { }

    protected void addStaticFunctions() { }

    protected void addOptionalFunctionality() { }

    protected void addGeneratedAnnotation() {
        importsManager.addImport("javax.annotation.processing.Generated");

        String annotation = "@Generated(value = \"" +
                enumName +
                "\", date = \"" +
                Instant.now().toString() +
                "\", comments = \"EDITABLE," +
                Version.get() +
                "\")";

        javaEnum.annotate(annotation);
    }

}
