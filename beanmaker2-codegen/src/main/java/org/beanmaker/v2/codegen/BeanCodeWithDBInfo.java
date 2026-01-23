package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.FunctionDeclaration;
import org.jcodegen.java.ObjectCreation;
import org.jcodegen.java.ReturnStatement;
import org.jcodegen.java.StringOrCode;
import org.jcodegen.java.VarDeclaration;
import org.jcodegen.java.Visibility;

import java.util.List;

import static org.beanmaker.v2.util.Strings.capitalize;
import static org.beanmaker.v2.util.Strings.quickQuote;

public abstract class BeanCodeWithDBInfo extends BeanCode {

    protected final Columns columns;
    protected final String tableName;

    public BeanCodeWithDBInfo(
            String beanName,
            String packageName,
            String namePrefix,
            String nameSuffix,
            Columns columns,
            ProjectParameters projectParameters)
    {
        super(beanName, packageName, namePrefix, nameSuffix, projectParameters);

        var errors = columns.getFormatErrors();
        if (!errors.isEmpty())
            throw new IllegalArgumentException(composeColumnErrorsExceptionText(errors));

        this.columns = columns;
        tableName = columns.getTable();
    }

    private String composeColumnErrorsExceptionText(List<FieldFormatError.FieldAssociatedError> errors) {
        var errorText = new StringBuilder();
        errorText.append("Field errors: ");
        for (var error: errors)
            errorText.append(error.formatMessage()).append(", ");
        errorText.delete(errorText.length() - 2, errorText.length());
        return errorText.toString();
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

        var getter = new FunctionDeclaration(getterPrefix + capitalize(name), type)
                .annotate("@Override")
                .visibility(Visibility.PUBLIC);
        if (TEMPORAL_TYPES.contains(type))
            getter.addContent(new ReturnStatement(new FunctionCall("copy", "DBUtil").addArgument(name)));
        else
            getter.addContent(new ReturnStatement(name));

        javaClass.addContent(getter).addContent(EMPTY_LINE);

        if (type.equals("Integer") || type.equals("Long") || TEMPORAL_TYPES.contains(type)
                || type.equals("Money") || type.equals("DecimalValue"))
        {
            addStrGetter(column);
        }

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

    protected String getSafeLabelFunctionName(String originalFunctionName) {
        if (originalFunctionName.endsWith("Label"))
            return originalFunctionName.substring(0, originalFunctionName.length() - "Label".length()) + "SafeLabel";

        throw new AssertionError(
                "Function name " + originalFunctionName + " not supported. Must end with 'Label'.");
    }

    protected void addFileGetterFunction(Column column) {
        String name = column.getJavaName();
        javaClass
                .addContent(new FunctionDeclaration("get" + chopID(name), "DbBeanFile")
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new FunctionCall("get", "LocalFileManager")
                                .addArgument(name))))
                .addContent(EMPTY_LINE);
    }

    protected void addBeanGetterFunction(Column column) {
        String type = column.isOriginalBeanId() ? beanName : column.getAssociatedBeanClass();
        String name = column.getJavaName();
        javaClass
                .addContent(new FunctionDeclaration("get" + chopID(name), type)
                        .annotate("@Override")
                        .visibility(Visibility.PUBLIC)
                        .addContent(new ReturnStatement(new ObjectCreation(type).addArgument(name))))
                .addContent(EMPTY_LINE);
    }

    protected void addLatestVersionCheckFunctions(String parameterArgument) {
        javaClass
                .addContent(
                        getLatestVersionFunctionDeclaration(false)
                                .addContent(getLatestVersionedBeanCheck(parameterArgument, "dbAccess"))
                )
                .addContent(EMPTY_LINE)
                .addContent(
                        getLatestVersionFunctionDeclaration(true)
                                .addContent(getLatestVersionedBeanCheck(parameterArgument, "transaction"))
                )
                .addContent(EMPTY_LINE);
    }

    protected FunctionDeclaration getLatestVersionFunctionDeclaration(boolean transaction) {
        var function = new FunctionDeclaration("isLatestVersionedBean", "boolean")
                .annotate("@Override")
                .visibility(Visibility.PUBLIC);

        if (transaction)
            function.addArgument(new FunctionArgument("DBTransaction", "transaction"));

        return function;
    }

    protected ReturnStatement getLatestVersionedBeanCheck(String parameters, String dbArgument) {
        return new ReturnStatement(
                new FunctionCall("isLatestVersionedBean", "VersionedBean")
                        .addArguments("this", parameters, dbArgument)
        );
    }

    protected void addIsVersionedBeanComponentOverloadedFunction() {
        columns.getItemOrderColumn().ifPresent(column -> {
            if (!Strings.isEmpty(column.getItemOrderAssociatedField())) {
                javaClass.addContent(
                        new FunctionDeclaration("isVersionedBeanComponent", "boolean")
                                .annotate("@Override")
                                .visibility(Visibility.PUBLIC)
                                .addContent(new ReturnStatement("true"))
                ).addContent(EMPTY_LINE);
            }
        });
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

    protected void insertUniqueCodeFunction(String beanRetrievalFunction) {
        if (beanRetrievalFunction != null) {
            javaClass.addContent(new FunctionDeclaration("getFromCode", "Optional<" + beanName + ">")
                            .visibility(Visibility.PUBLIC)
                            .markAsStatic()
                            .addArgument(new FunctionArgument("String", "code"))
                            .addContent(new ReturnStatement(new FunctionCall(beanRetrievalFunction, "Codes")
                                    .addArgument(beanName + ".class")
                                    .addArgument(beanName + "Parameters.INSTANCE")
                                    .addArgument("code")
                                    .addArgument("DbBeans.dbAccess"))))
                    .addContent(EMPTY_LINE);
        }
    }

}
