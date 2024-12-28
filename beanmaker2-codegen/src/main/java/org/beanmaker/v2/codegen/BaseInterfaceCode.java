package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Version;
import org.jcodegen.java.EmptyLine;
import org.jcodegen.java.ImportsManager;
import org.jcodegen.java.InterfaceSourceFile;
import org.jcodegen.java.JavaInterface;

import java.time.Instant;
import java.util.List;

import static org.beanmaker.v2.util.Strings.uncapitalize;

public abstract class BaseInterfaceCode implements BeanMakerSourceFile {

    protected final String beanName;
    protected final String beanVarName;
    protected final String packageName;
    protected final String interfaceName;
    protected final Columns columns;
    protected final ProjectParameters projectParameters;

    protected final InterfaceSourceFile sourceFile;
    protected final JavaInterface javaInterface;
    protected final ImportsManager importsManager;

    protected static final EmptyLine EMPTY_LINE = new EmptyLine();

    public BaseInterfaceCode(String beanName, String packageName, String interfaceNameExtension, Columns columns, ProjectParameters projectParameters) {
        this.beanName = beanName;
        beanVarName = uncapitalize(beanName);
        this.packageName = packageName;
        interfaceName = beanName + interfaceNameExtension;
        this.columns = columns;
        this.projectParameters = projectParameters;

        sourceFile = new InterfaceSourceFile(packageName, interfaceName);
        javaInterface = sourceFile.getJavaInterface();
        javaInterface.addContent(EMPTY_LINE);
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
        decorateJavaInterface();
        addStaticProperties();
        addStaticInitialization();
        addFunctionDeclarations();
        addCoreFunctionality();
    }

    protected void addImports() { }

    @SafeVarargs
    protected final void addImports(List<String>... importLists) {
        for (var importList: importLists)
            for (String importname : importList)
                importsManager.addImport(importname);
    }

    protected void decorateJavaInterface() { }

    protected void addStaticProperties() { }

    protected void addStaticInitialization() { }

    protected abstract void addFunctionDeclarations();

    protected void addCoreFunctionality() { }

    protected void addGeneratedAnnotation() {
        importsManager.addImport("org.beanmaker.v2.runtime.annotations.DoNotEdit");

        String annotation = "@DoNotEdit(generator = \"%s\", version = \"%s\", date = \"%s\")".formatted(
                getClass().getName(), Version.get(), Instant.now().toString());

        javaInterface.annotate(annotation);
    }

}
