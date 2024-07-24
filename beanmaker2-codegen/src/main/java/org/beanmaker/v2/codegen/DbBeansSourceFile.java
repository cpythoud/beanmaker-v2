package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.java.Assignment;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.StaticBlock;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

public class DbBeansSourceFile extends BaseCode {

    private final String database;
    private final String packageName;

    public DbBeansSourceFile(String database, String packageName) {
        this(database, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public DbBeansSourceFile(String database, String packageName, ProjectParameters projectParameters) {
        super("DbBeans", packageName, projectParameters);

        if (Strings.isEmpty(database))
            throw new IllegalArgumentException("database empty");

        this.database = database;
        this.packageName = packageName;

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.dbbeans.sql.DB");
        importsManager.addImport("org.dbbeans.sql.DBAccess");
        importsManager.addImport("org.dbbeans.sql.DBTransaction");
        if (projectParameters.createDatabaseProviderReference()) {
            importsManager.addImport("org.beanmaker.v2.runtime.DatabaseProvider");
            importsManager.addImport("org.beanmaker.v2.util.Types");
        } else {
            importsManager.addImport("org.dbbeans.sql.DBFromDataSource");
        }
    }

    @Override
    protected void addStaticProperties() {
        javaClass
                .addContent(new VarDeclaration("DB", "db").markAsStatic().markAsFinal())
                .addContent(new VarDeclaration("DBAccess", "dbAccess").markAsStatic().markAsFinal())
                .addContent(EMPTY_LINE);

        if (projectParameters.createDatabaseProviderReference())
            javaClass.addContent(new VarDeclaration(
                    "String",
                    "DB_PROVIDER_CLASS",
                    Strings.quickQuote(packageName + ".config.DBProvider"))
                    .markAsStatic()
                    .markAsFinal()
                    .visibility(Visibility.PRIVATE));
        else
            javaClass.addContent(new VarDeclaration(
                    "String",
                    "DATA_SOURCE",
                    Strings.quickQuote("java:comp/env/jdbc/" + database))
                    .markAsStatic()
                    .markAsFinal()
                    .visibility(Visibility.PRIVATE));

        javaClass.addContent(EMPTY_LINE);
    }

    @Override
    protected void addStaticInitialization() {
        if (projectParameters.createDatabaseProviderReference())
            addClassNameInitialization();
        else
            addDataSourceNameInitialization();
    }

    private void addDataSourceNameInitialization() {
        javaClass
                .addContent(new StaticBlock()
                        .addContent(new Assignment(
                                "db",
                                new ObjectCreation("DBFromDataSource").addArgument("DATA_SOURCE")))
                        .addContent(new Assignment(
                                "dbAccess",
                                new ObjectCreation("DBAccess").addArgument("db"))))
                .addContent(EMPTY_LINE);
    }

    private void addClassNameInitialization() {
        javaClass
                .addContent(new StaticBlock()
                        .addContent(new VarDeclaration(
                                "var",
                                "dbProvider",
                                new FunctionCall("createInstanceOf", "Types")
                                        .addArguments("DB_PROVIDER_CLASS", "DatabaseProvider.class")))
                        .addContent(new Assignment(
                                "db",
                                new FunctionCall("getDatabaseReference", "dbProvider")))
                        .addContent(new Assignment(
                                "dbAccess",
                                new ObjectCreation("DBAccess").addArgument("db"))))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() {
        javaClass
                .addContent(new FunctionDeclaration("createDBTransaction", "DBTransaction")
                        .markAsStatic()
                        .addContent(new ReturnStatement(new ObjectCreation("DBTransaction").addArgument("db"))))
                .addContent(EMPTY_LINE);
    }

}
