package org.beanmaker.v2.codegen.java;

public class ClassSourceFile extends SourceFile {

    private final String className;

    private final JavaClass javaClass;
    private final ImportsManager importsManager = new ImportsManager();


    public ClassSourceFile(final String packageName, final String className) {
        super(packageName);
        this.className = className;
        this.javaClass = new JavaClass(className);
    }

    public JavaClass getJavaClass() {
        return javaClass;
    }

    @Override
    protected String getName() {
        return className;
    }

    @Override
    protected void addMainCode(StringBuilder buf) {
        javaClass.setIndentationLevel(0);  // sanitize indentation ?
        buf.append(javaClass.toString());
    }

}
