package org.beanmaker.v2.codegen;

import org.jcodegen.java.EmptyLine;
import org.jcodegen.java.ImportsManager;
import org.jcodegen.java.InterfaceSourceFile;
import org.jcodegen.java.JavaInterface;

import static org.beanmaker.v2.util.Strings.uncapitalize;

public abstract class BaseInterfaceCode implements BeanMakerSourceFile {

    protected final String beanName;
    protected final String beanVarName;
    protected final String packageName;
    protected final String interfaceName;
    protected final Columns columns;

    protected final InterfaceSourceFile sourceFile;
    protected final JavaInterface javaInterface;
    protected final ImportsManager importsManager;

    protected static final EmptyLine EMPTY_LINE = new EmptyLine();

    public BaseInterfaceCode(String beanName, String packageName, String interfaceNameExtension, Columns columns) {
        this.beanName = beanName;
        beanVarName = uncapitalize(beanName);
        this.packageName = packageName;
        interfaceName = beanName + interfaceNameExtension;
        this.columns = columns;

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
    }

    protected void addImports() { }

    protected void decorateJavaInterface() { }

    protected void addStaticProperties() { }

    protected void addStaticInitialization() { }

    protected abstract void addFunctionDeclarations();

}
