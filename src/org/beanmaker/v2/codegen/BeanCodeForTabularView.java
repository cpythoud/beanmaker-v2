package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Strings;
import org.jcodegen.java.FunctionCall;

public abstract class BeanCodeForTabularView extends BeanCodeWithDBInfo {

    private final String beanName;
    private final String bundleName;

    public BeanCodeForTabularView(
            final String beanName,
            final String tabularBeanType,
            final String packageName,
            final String nameExtension,
            final Columns columns,
            final String tableName)
    {
        super(beanName + tabularBeanType, packageName, nameExtension, columns);

        this.beanName = beanName;
        bundleName = getBundleName(beanName,  packageName);
    }

    protected void addConstructor() {
        javaClass.addContent(
                javaClass.createConstructor().addContent(
                        new FunctionCall("super")
                                .byItself()
                                .addArguments(Strings.quickQuote(bundleName), beanName + ".DATABASE_TABLE_NAME")
                )
        ).addContent(EMPTY_LINE);
    }

}
