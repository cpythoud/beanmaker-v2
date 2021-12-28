package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.java.Assignment;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.StaticBlock;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

public class DbBeanSourceFile extends BaseCode {

    private final String database;

    public DbBeanSourceFile(String database, String packageName) {
        super("DbBeans", packageName);

        if (Strings.isEmpty(database))
            throw new IllegalArgumentException("database empty");

        this.database = database;

        createSourceCode();
    }

    private void createSourceCode() {
        sourceFile.setStartComment(SourceFiles.getCommentAndVersion());

        addImports();
        javaClass.markAsAbstract();
        addProperties();
        addStaticInitialization();
        addTransactionGetter();
    }

    private void addImports() {
        importsManager.addImport("org.dbbeans.sql.DB");
        importsManager.addImport("org.dbbeans.sql.DBAccess");
        importsManager.addImport("org.dbbeans.sql.DBFromDataSource");
        importsManager.addImport("org.dbbeans.sql.DBTransaction");
    }

    private void addProperties() {
        javaClass
                .addContent(new VarDeclaration("DB", "db").markAsStatic().markAsFinal())
                .addContent(new VarDeclaration("DBAccess", "dbAccess").markAsStatic().markAsFinal())
                .addContent(EMPTY_LINE)
                .addContent(new VarDeclaration("String", "DATA_SOURCE", Strings.quickQuote("java:comp/env/jdbc/" + database))
                        .markAsStatic().markAsFinal().visibility(Visibility.PRIVATE))
                .addContent(EMPTY_LINE);
    }

    private void addStaticInitialization() {
        javaClass
                .addContent(new StaticBlock()
                        .addContent(new Assignment("db", new ObjectCreation("DBFromDataSource").addArgument("DATA_SOURCE")))
                        .addContent(new Assignment("dbAccess", new ObjectCreation("DBAccess").addArgument("db"))))
                .addContent(EMPTY_LINE);
    }

    private void addTransactionGetter() {
        javaClass
                .addContent(new FunctionDeclaration("createDBTransaction", "DBTransaction")
                        .markAsStatic()
                        .addContent(new ReturnStatement(new ObjectCreation("DBTransaction").addArgument("db"))));
    }

}
