package org.beanmaker.v2.codegen;

import org.jcodegen.java.EmptyLine;
import org.jcodegen.java.ImportsManager;
import org.jcodegen.java.InterfaceSourceFile;
import org.jcodegen.java.JavaInterface;

import java.util.List;

import static org.beanmaker.v2.util.Strings.uncapitalize;

// ! There are no longer any class extending this one in the project. We keep it for now, in case we need
// ! to reintroduce interfaces in the generated code.
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

}
