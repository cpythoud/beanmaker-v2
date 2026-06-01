package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ColgroupTag extends ColElement<ColgroupTag> {

    public ColgroupTag() {
        element = new XMLElement("colgroup", false);
    }

    public ColgroupTag(final int span) {
        this();
        span(span);
    }

    @Override
    protected ColgroupTag getThis() {
        return this;
    }

    public ColgroupTag span(final int value) {
        return attribute("span", Integer.toString(value));
    }

    public ColgroupTag width(final String value) {
        return attribute("width", value);
    }

    public ColgroupTag child(final ColTag col) {
        return super.child(col);
    }
}
