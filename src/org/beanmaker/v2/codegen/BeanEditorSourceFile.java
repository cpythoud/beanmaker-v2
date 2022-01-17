package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;

public class BeanEditorSourceFile extends BeanCodeWithDBInfo {

    public BeanEditorSourceFile(String beanName, String packageName, Columns columns) {
        super(beanName, packageName, "Editor", columns);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("java.sql.ResultSet");
        for (Column column: columns.getList()) {
            if (column.getJavaType().equals("Date"))
                importsManager.addImport("java.sql.Date");
            if (column.getJavaType().equals("Time"))
                importsManager.addImport("java.sql.Time");
            if (column.getJavaType().equals("Timestamp"))
                importsManager.addImport("java.sql.Timestamp");
        }
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.extendsClass(beanName + "EditorBase");
    }

    @Override
    protected void addConstructors() {
        addNoParamConstructor();
        addIDConstructor();
        addBeanConstructor();
        addFieldsConstructor();
        addRSConstructor();
    }

    private void addNoParamConstructor() {
        javaClass
                .addContent(javaClass.createConstructor())
                .addContent(EMPTY_LINE);
    }

    private void addIDConstructor() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument("long", "id"))
                        .addContent(new FunctionCall("super").byItself().addArgument("id")))
                .addContent(EMPTY_LINE);
    }

    private void addBeanConstructor() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument(beanName, beanVarName))
                        .addContent(new FunctionCall("super").byItself().addArgument(beanVarName)))
                .addContent(EMPTY_LINE);
    }

    private void addFieldsConstructor() {
        var constructor = javaClass.createConstructor();
        var superCall = new FunctionCall("super").byItself();
        for (Column column: columns.getList()) {
            String type = column.getJavaType();
            String name = column.getJavaName();
            constructor.addArgument(new FunctionArgument(type, name));
            superCall.addArgument(name);
            if (type.equals("Money"))
                importsManager.addImport("org.beanmaker.v2.util.Money");
        }

        javaClass
                .addContent(constructor.addContent(superCall))
                .addContent(EMPTY_LINE);
    }

    private void addRSConstructor() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .addArgument(new FunctionArgument("ResultSet", "rs"))
                        .addContent(new FunctionCall("super").byItself().addArgument("rs")))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
