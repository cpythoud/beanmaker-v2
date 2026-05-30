package org.beanmaker.v2.codegen.java;

public class InterfaceSourceFile extends SourceFile {

    private final String interfaceName;
    private final JavaInterface javaInterface;

    public InterfaceSourceFile(String packageName, String interfaceName) {
        super(packageName);
        this.interfaceName = interfaceName;
        this.javaInterface = new JavaInterface(interfaceName);
    }

    public JavaInterface getJavaInterface() {
        return javaInterface;
    }

    @Override
    protected String getName() {
        return interfaceName;
    }

    @Override
    protected void addMainCode(StringBuilder buf) {
        javaInterface.setIndentationLevel(0);  // ? sanitize indentation?
        buf.append(javaInterface);
    }

}
