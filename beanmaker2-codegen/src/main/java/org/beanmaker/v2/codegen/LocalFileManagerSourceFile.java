package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.Visibility;

import java.util.List;

public class LocalFileManagerSourceFile extends BaseCode {

    private static final List<String> BM_RUNTIME_IMPORTS =
            createImportList("org.beanmaker.v2.runtime", "DbBeanFile", "DbBeanFileEditor",
                    "DbBeanFileInternalFilenameCalculator", "MissingImplementationException");

    private static final FunctionArgument ID_ARG = new FunctionArgument("long", "id");
    private static final FunctionArgument TRANSACTION_ARG = new FunctionArgument("DBTransaction", "transaction");

    public LocalFileManagerSourceFile(String packageName) {
        this(packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public LocalFileManagerSourceFile(String packageName, ProjectParameters projectParameters) {
        super("LocalFileManager", packageName, projectParameters);

        createSourceCode();
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC);
    }

    @Override
    protected void addImports() {
        addImports(BM_RUNTIME_IMPORTS);
        importsManager.addImport("org.dbbeans.sql.DBTransaction");
    }

    @Override
    protected void addCoreFunctionality() {
        addNonImplementedStaticFunction("DbBeanFile", "get", ID_ARG);
        addNonImplementedStaticFunction("DbBeanFileEditor", "getOrCreateEditor", ID_ARG);
        addNonImplementedStaticFunction("boolean", "isIdOK", ID_ARG);
        addNonImplementedStaticFunction("boolean", "isIdOK", ID_ARG, TRANSACTION_ARG);
        addNonImplementedStaticFunction("String", "getDefaultUploadDir");
        addNonImplementedStaticFunction("DbBeanFileInternalFilenameCalculator", "getInternalFileNameCalculator");
        addNonImplementedStaticFunction("int", "getSubDirFileCountThreshold");
        addNonImplementedStaticFunction("String", "getFilename", ID_ARG);
    }

}
