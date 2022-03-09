package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;

import java.util.List;

public abstract class BeanCode extends BaseCode {

    protected static final List<String> TEMPORAL_TYPES = List.of("Date", "Time", "Timestamp");

    protected final String beanName;
    protected final String packageName;
    protected final String beanVarName;
    protected final String bundleName;
    protected final String parametersInstanceExpression;
    protected final String badIDExceptionMessage;
    protected final String itemManagerRetrievalCall;
    protected final String formatterInstanceExpression;

    public BeanCode(String beanName, String packageName, String namePrefix, String nameSuffix) {
        this(beanName, packageName, namePrefix, nameSuffix, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanCode(String beanName, String packageName, String namePrefix, String nameSuffix, ProjectParameters projectParameters) {
        super((namePrefix == null ? "" : namePrefix) + beanName + (nameSuffix == null ? "" : nameSuffix),  packageName, projectParameters);
        this.beanName = beanName;
        this.packageName = packageName;
        beanVarName = getBeanVarName(beanName);
        bundleName = getBundleName(beanName,  packageName);
        parametersInstanceExpression = getParametersInstanceExpression(beanName);
        badIDExceptionMessage = getBadIDExceptionMessage(beanName);
        itemManagerRetrievalCall = getItemManagerRetrievalCall(beanName);
        formatterInstanceExpression = getFormatterInstanceExpression(beanName);
    }

    protected static String getBeanVarName(String beanName) {
        return Strings.uncapitalize(beanName);
    }

    protected static String getBundleName(String beanName, String packageName) {
        return Strings.replace(packageName, ".", "-") + "-" + beanName;
    }

    protected static String getParametersInstanceExpression(String beanName) {
        return beanName + "Parameters.INSTANCE";
    }

    protected static String getBadIDExceptionMessage(String beanName) {
        return "\"No " + beanName + " with ID #\" + id";
    }

    public static String chopID(String varName) {
        if (varName == null || varName.length() < 3 || !varName.startsWith("id"))
            throw new IllegalArgumentException("Illegal chop ID operation on variable name: " + varName);

        return varName.substring(2);
    }

    protected static String getItemManagerRetrievalCall(String beanName) {
        return getParametersInstanceExpression(beanName) + ".getItemOrderManager()";
    }

    protected static String getFormatterInstanceExpression(String beanName) {
        return beanName + "Formatter.INSTANCE";
    }

    protected void applySealedModifier(String permittedClass) {
        if (projectParameters.createSealedClasses())
            javaClass.permitExtension(permittedClass);
    }

}
