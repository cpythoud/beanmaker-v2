package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import org.jcodegen.java.Condition;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.IfBlock;

import java.util.Arrays;
import java.util.List;

public abstract class BeanCodeWithDBInfo extends BeanCode {

    protected final Columns columns;
    protected final String tableName;

    protected static final List<String> JAVA_TEMPORAL_TYPES = Arrays.asList("Date", "Time", "Timestamp");

    public BeanCodeWithDBInfo(String beanName, String packageName, String nameExtension, Columns columns) {
        super(beanName, packageName, nameExtension);

        if (!columns.isOK())
            throw new IllegalArgumentException("columns not ok");

        this.columns = columns;
        tableName = columns.getTable();
    }

    protected IfBlock ifNotDataOK() {
        return ifNotDataOK(false);
    }

    protected IfBlock ifNotDataOK(boolean fromBean) {
        FunctionCall functionCall;
        if (fromBean)
            functionCall = new FunctionCall("isDataOK", beanVarName);
        else
            functionCall = new FunctionCall("isDataOK");

        return new IfBlock(new Condition(functionCall, true));
    }

    protected FunctionCall getFilenameFunctionCall(String bean, String field) {
        return new FunctionCall("getFilename", "LocalFiles")
                .addArgument(new FunctionCall("get" + Strings.capitalize(field), bean));
    }

}
