package org.beanmaker.v2.codegen.java;

public class EnumSourceFile extends SourceFile {

    private final String enumName;

    private final JavaEnum javaEnum;


    public EnumSourceFile(String packageName, String enumName) {
        super(packageName);
        this.enumName = enumName;
        this.javaEnum = new JavaEnum(enumName);
    }

    public JavaEnum getJavaEnum() {
        return javaEnum;
    }

    @Override
    protected String getName() {
        return enumName;
    }

    @Override
    protected void addMainCode(StringBuilder buf) {
        javaEnum.setIndentationLevel(0);  // * sanitize indentation
        buf.append(javaEnum);
    }

}
