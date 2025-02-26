package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.StringOrCode;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import static org.beanmaker.v2.util.Strings.capitalize;
import static org.beanmaker.v2.util.Strings.quickQuote;

public abstract class BeanCodeWithDBInfo extends BeanCode {

    protected final Columns columns;
    protected final String tableName;

    public BeanCodeWithDBInfo(String beanName, String packageName, String namePrefix, String nameSuffix, Columns columns, ProjectParameters projectParameters) {
        super(beanName, packageName, namePrefix, nameSuffix, projectParameters);

        if (!columns.isOK())
            throw new IllegalArgumentException("columns not ok");

        this.columns = columns;
        tableName = columns.getTable();
    }

    protected void addProperty(String type, String name, boolean isFinal, StringOrCode<FunctionCall> initializer) {
        VarDeclaration varDeclaration;
        if (initializer == null)
            varDeclaration = new VarDeclaration(type, name);
        else if (initializer.isString())
            varDeclaration = new VarDeclaration(type, name, initializer.getString());
        else if (initializer.isCode())
            varDeclaration = new VarDeclaration(type, name, initializer.getCode());
        else
            throw new AssertionError("Unexpected status of initializer");

        if (isFinal)
            varDeclaration.markAsFinal();

        javaClass.addContent(varDeclaration.visibility(Visibility.PRIVATE));
    }

    protected void addGetter(Column column) {
        String type = column.getJavaType();
        String name = column.getJavaName();
        String getterPrefix = (type.equals("Boolean") || type.equals("boolean")) ? "is" : "get";

        var getter = new FunctionDeclaration(getterPrefix + capitalize(name), type).visibility(Visibility.PUBLIC);
        if (column.isId() || column.isItemOrder() || name.equals("idLabel"))
            getter.annotate("@Override");
        if (TEMPORAL_TYPES.contains(type))
            getter.addContent(new ReturnStatement(new FunctionCall("copy", "DBUtil").addArgument(name)));
        else
            getter.addContent(new ReturnStatement(name));

        javaClass.addContent(getter).addContent(EMPTY_LINE);

        if (type.equals("Integer") || type.equals("Long") || TEMPORAL_TYPES.contains(type) || type.equals("Money"))
            addStrGetter(column);

        if (column.isBeanReference()) {
            if (column.isLabelReference())
                addLabelSpecificGetterFunctions(column);
            else if (column.isFileReference())
                addFileGetterFunction(column);
            else if (!column.isId())
                addBeanGetterFunction(column);
        }

        if (column.isItemOrder()) {
            addItemOrderEdgeStatusCheckFunctions();

            if (!Strings.isEmpty(column.getItemOrderAssociatedField()))
                javaClass
                        .addContent(new FunctionDeclaration("getItemOrderSecondaryFieldID", "long")
                                .annotate("@Override")
                                .visibility(Visibility.PUBLIC)
                                .addContent(new ReturnStatement(
                                        new FunctionCall(
                                                "get" + capitalize(getItemOrderSecondaryFieldJavaName(column))))))
                        .addContent(EMPTY_LINE);
        }
    }

    // ! Only applies to BeanBase & BeanBaseEditor, must be implemented in super class
    protected void addStrGetter(Column column) {
        throw new UnsupportedOperationException();
    }

    // ! Only applies to BeanBase & BeanBaseEditor, must be implemented in super class
    protected void addLabelSpecificGetterFunctions(Column column) {
        throw new UnsupportedOperationException();
    }

    protected void addFileGetterFunction(Column column) {
        String name = column.getJavaName();
        javaClass
                .addContent(new FunctionDeclaration("get" + chopID(name), "DbBeanFile")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("get", "LocalFileManager")
                                .addArgument(name))))
                .addContent(EMPTY_LINE);
    }

    protected void addBeanGetterFunction(Column column) {
        String type = column.getAssociatedBeanClass();
        String name = column.getJavaName();
        javaClass
                .addContent(new FunctionDeclaration("get" + chopID(name), type)
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new ObjectCreation(type).addArgument(name))))
                .addContent(EMPTY_LINE);
    }

    protected void addItemOrderEdgeStatusCheckFunctions() {
        javaClass
                .addContent(new FunctionDeclaration("isFirstInItemOrder", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .annotate("@Override")
                        .addContent(new ReturnStatement(
                                new FunctionCall("isFirstInItemOrder", itemManagerRetrievalCall)
                                        .addArgument("this"))))
                .addContent(EMPTY_LINE)
                .addContent(new FunctionDeclaration("isLastInItemOrder", "boolean")
                        .visibility(Visibility.PUBLIC)
                        .annotate("@Override")
                        .addContent(new ReturnStatement(
                                new FunctionCall("isLastInItemOrder", itemManagerRetrievalCall)
                                        .addArguments("this", "dbAccess"))))
                .addContent(EMPTY_LINE);
    }

    protected String getItemOrderSecondaryFieldJavaName(Column column) {
        // !!! We assume the name of the field is the suggested name from Beanmaker.
        // !!! This might prove incorrect at same point and need an extension of the Column class
        // !!! as well as an adjustment of the related user interfaces.
        return Strings.uncapitalize(Strings.camelize(column.getItemOrderAssociatedField()));
    }

    protected void addListAndCountOfBeansInRelationshipFunctions(Visibility visibility) {
        for (OneToManyRelationship relationship: columns.getOneToManyRelationships()) {
            addListOfBeansInRelationshipFunction(relationship, visibility);
            addCountOfBeansInRelationshipFunction(relationship, visibility);
        }
    }

    private void addListOfBeansInRelationshipFunction(OneToManyRelationship relationship, Visibility visibility) {
        String type = relationship.getBeanClass();
        javaClass
                .addContent(new FunctionDeclaration("get" + capitalize(relationship.getJavaName()), "List<" + type + ">")
                        .visibility(visibility)
                        .addContent(new ReturnStatement(new FunctionCall("getInventory", "DBUtil")
                                .addArgument(getParametersInstanceExpression(type))
                                .addArgument(quickQuote(relationship.getIdSqlName()))
                                .addArgument(new FunctionCall("getId"))
                                .addArgument(type + "::getList")
                                .addArgument("dbAccess"))))
                .addContent(EMPTY_LINE);
    }

    private void addCountOfBeansInRelationshipFunction(OneToManyRelationship relationship, Visibility visibility) {
        String type = relationship.getBeanClass();
        javaClass
                .addContent(new FunctionDeclaration("getCountFor" + capitalize(relationship.getJavaName()), "long")
                        .visibility(visibility)
                        .addContent(new ReturnStatement(new FunctionCall("getInventorySize", "DBUtil")
                                .addArgument(getParametersInstanceExpression(type))
                                .addArgument(quickQuote(relationship.getIdSqlName()))
                                .addArgument(new FunctionCall("getId"))
                                .addArgument("dbAccess"))))
                .addContent(EMPTY_LINE);
    }

}
