package org.beanmaker.v2.codegen.html.xmlbase;

/**
 * ...
 */
public abstract class XMLAttribute {

    public final String name;

    public XMLAttribute(String name) {
        this.name = name;
    }

    public abstract  XMLAttribute copy();

}
