package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public abstract class JavaCodeBlock <T extends JavaCodeBlock<T>> {

    private final List<JavaCodeBlock> content = new ArrayList<>();

    private final String keyword;
    private int indentationLevel;


    public JavaCodeBlock(String keyword, int indentationLevel) {
        this.keyword = keyword;
        this.indentationLevel = indentationLevel;
    }

    protected abstract T getThis();


    public String getKeyword() {
        return keyword;
    }

    public int getIndentationLevel() {
        return indentationLevel;
    }

    public void setIndentationLevel(int indentationLevel) {
        this.indentationLevel = indentationLevel;
        for (JavaCodeBlock block: content)
            block.setIndentationLevel(indentationLevel + 1);
    }

    public String getTabs() {
        return getTabs(indentationLevel);
    }

    public static String getTabs(int indentationLevel) {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < indentationLevel; i++)
            buf.append("\t");

        return buf.toString();
    }


    public T addContent(JavaCodeBlock codeBlock) {
        codeBlock.setIndentationLevel(getIndentationLevel() + 1);
        content.add(codeBlock);
        return getThis();
    }

    public T addContent(String code) {
        content.add(new LineOfCode(code, getIndentationLevel() + 1));
        return getThis();
    }


    protected boolean hasContent() {
        return !content.isEmpty();
    }

    protected boolean contentIsAOneLiner() {
        return content.size() == 1;
    }

    protected void appendContent(StringBuilder buf) {
        if (content.isEmpty()) {
            buf.append("{ }");
            return;
        }

        buf.append("{\n");

        appendCodeBlocks(buf);

        buf.append(getTabs());
        buf.append("}");
    }

    protected void appendCodeBlocks(StringBuilder buf) {
        for (JavaCodeBlock javaCodeBlock: content)
            buf.append(javaCodeBlock);
    }

    protected void appendOneLinerContent(StringBuilder buf) {
        appendOneLinerContent(buf, true);
    }

    protected void appendOneLinerContent(StringBuilder buf, boolean withNewline) {
        if (!contentIsAOneLiner())
            throw new IllegalArgumentException("Block content is more than one line/unit of code or is empty. Cannot be printed as a one liner.");

        if (withNewline)
            buf.append("\n");
        buf.append(content.get(0));
    }

    protected void appendCommaSeparatedListItems(StringBuilder buf, List<String> items) {
        for (String item: items) {
            buf.append(item);
            buf.append(", ");
        }
        buf.delete(buf.length() - 2, buf.length());
    }


    protected boolean contentContainsBlock(String keyword) {
        for (JavaCodeBlock codeBlock: content)
            if (codeBlock.keyword.equals(keyword))
                return true;

        return false;
    }
}
