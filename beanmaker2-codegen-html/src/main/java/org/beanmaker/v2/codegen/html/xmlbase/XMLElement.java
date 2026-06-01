package org.beanmaker.v2.codegen.html.xmlbase;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class XMLElement extends CodeBlock {

    public final String name;
    public final String body;

    private final List<XMLAttribute> attributes = new ArrayList<>();
    private final List<XMLElement> children = new ArrayList<>();

    private final boolean selfClosing;

    private boolean onOneLine = false;

    public static final String CDATA = "CDATA";


    public XMLElement(String name) {
        this(name, 0);
    }

    public XMLElement(String name, boolean selfClosing) {
        this(name, 0, selfClosing);
    }

    public XMLElement(String name, int indentation) {
        this(name, null, indentation);
    }

    public XMLElement(String name, int indentation, boolean selfClosing) {
        this(name, null, indentation, selfClosing);
    }

    public XMLElement(String name, String body) {
        this(name, body, 0);
    }

    public XMLElement(String name, String body, boolean selfClosing) {
        this(name, body, 0, selfClosing);
    }

    public XMLElement(String name, String body, int indentation) {
        this(name, body, indentation, true);
    }

    public XMLElement(String name, String body, int indentation, boolean selfClosing) {
        super(indentation);
        this.name = name;
        this.body = body;
        this.selfClosing = selfClosing;

        if (name.equals(CDATA) && body == null)
            throw new IllegalArgumentException("CDATA element cannot be empty");
    }


    public boolean isOnOneLine() {
        return onOneLine;
    }

    public void setOnOneLine(boolean onOneLine) {
        //System.out.println("On one line : " + name);
        this.onOneLine = onOneLine;
    }

    public void addAttribute(XMLAttribute attribute) {
        if (hasAttribute(attribute.name))
            throw new IllegalArgumentException("XMLAttribute already defined");

        attributes.add(attribute);
    }

    public boolean hasAttribute(String name) {
        for (XMLAttribute attribute: attributes)
            if (attribute.name.equals(name))
                return true;

        return false;
    }

    public void removeAttribute(String name) {
        if (!hasAttribute(name))
            throw new IllegalArgumentException("Element doesn't have attribute: " + name);

        for (XMLAttribute attribute: attributes)
            if (attribute.name.equals(name)) {
                attributes.remove(attribute);
                break;
            }
    }

    public void replaceAttribute(String name, XMLAttribute attribute) {
        if (!attribute.name.equals(name))
            throw new IllegalArgumentException("Name of replacement attribute (" + attribute.name + ") does not match name of replaced attribute (" + name + ")");

        removeAttribute(name);
        addAttribute(attribute);
    }

    public XMLAttribute getAttribute(String name) {
        for (XMLAttribute attribute: attributes)
            if (attribute.name.equals(name))
                return attribute.copy();

        return null;
    }

    public void addChild(XMLElement child) {
        children.add(child);
    }

    public void addChildren(List<XMLElement> children) {
        this.children.addAll(children);
    }

    @Override
    public String toString() {
        return toString(getIndentationLevel());
    }

    public String toString(int indentLevel) {
        return toString(indentLevel, onOneLine);
    }

    public String toString(int indentLevel, boolean onOneLineInherited) {
        boolean onOneLineLocally;
        if (this.onOneLine)
            onOneLineLocally = true;
        else
            onOneLineLocally = onOneLineInherited;

        if (indentLevel < 0)
            throw new IllegalArgumentException("indentLevel < 0");

        StringBuilder buf = new StringBuilder();

        buf.append(CodeBlock.getTabs(indentLevel));

        if (name.equals(CDATA)) {
            buf.append(body);
            if (!onOneLineLocally)
                buf.append("\n");
        } else {
            buf.append("<");
            buf.append(name);

            for (XMLAttribute attribute: attributes)
                buf.append(attribute);

            if (children.size() == 0 && body == null) {
                if (selfClosing) {
                    buf.append(" />");
                    if (!onOneLineLocally)
                        buf.append("\n");
                } else {
                    buf.append("></");
                    buf.append(name);
                    buf.append(">");
                    if (!onOneLineLocally)
                        buf.append("\n");
                }
            } else {
                buf.append(">");
                if (!onOneLineLocally)
                    buf.append("\n");

                for (XMLElement child: children)
                    buf.append(child.toString(indentLevel + 1, onOneLineLocally));

                if (body != null) {
                    if (!onOneLineLocally)
                        buf.append(CodeBlock.getTabs(indentLevel + 1));
                    buf.append(body);
                    if (!onOneLineLocally)
                        buf.append("\n");
                }

                if (!onOneLineLocally)
                    buf.append(CodeBlock.getTabs(indentLevel));
                buf.append("</");
                buf.append(name);
                buf.append(">\n");
            }
        }

        return buf.toString();
    }

    public String getOpeningTag() {
        if (name.equals(CDATA))
            throw new IllegalArgumentException("No opening tag for CDATA");

        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append(name);
        for (XMLAttribute attribute: attributes)
            buf.append(attribute);
        buf.append(">");

        return buf.toString();
    }

    public String getClosingTag() {
        if (name.equals(CDATA))
            throw new IllegalArgumentException("No opening tag for CDATA");

        return "</" + name + ">";
    }

    public static XMLElement getOneLiner(String name, String value) {
        XMLElement oneLiner = new XMLElement(name, value);
        oneLiner.setOnOneLine(true);
        return oneLiner;
    }

    public static XMLElement getOneLiner(String name, boolean value) {
        return getOneLiner(name, value ? "true" : "false");
    }

}
