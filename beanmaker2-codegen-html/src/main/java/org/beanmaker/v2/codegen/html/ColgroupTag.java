package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ColgroupTag extends ColElement<ColgroupTag> {

    public ColgroupTag() {
        element = new XMLElement("colgroup", false);
    }

    public ColgroupTag(int span) {
        this();
        span(span);
    }

    @Override
    protected ColgroupTag getThis() {
        return this;
    }

    public ColgroupTag span(int value) {
        return attribute("span", Integer.toString(value));
    }

    public ColgroupTag width(String value) {
        return attribute("width", value);
    }

    public ColgroupTag child(ColTag col) {
        return super.child(col);
    }

}
