package org.beanmaker.v2.codegen.java;

/**
 * ...
 */
public abstract class SourceFile {

    private final String packageName;

    private final ImportsManager importsManager = new ImportsManager();

    private String startComment;
    private String endComment;


    public SourceFile(String packageName) {
        this.packageName = packageName;
    }

    public ImportsManager getImportsManager() {
        return importsManager;
    }

    public String getFilename() {
        return getName() + ".java";
    }

    protected abstract String getName();


    public void setStartComment(String startComment) {
        this.startComment = startComment;
    }

    public void setEndComment(String endComment) {
        this.endComment = endComment;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        if (startComment != null)
            buf.append(startComment);

        appendPackageName(buf);
        buf.append(importsManager.getImports());

        addMainCode(buf);

        if (endComment != null)
            buf.append(endComment);

        return buf.toString();
    }

    protected abstract void addMainCode(StringBuilder buf);

    private void appendPackageName(StringBuilder buf) {
        buf.append("package ");
        buf.append(packageName);
        buf.append(";\n\n");
    }
}
