package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ColTag extends ColElement<ColTag> {

    public ColTag() {
        element = new XMLElement("coltag", false);
    }

    public ColTag(int span) {
        this();
        span(span);
    }

    @Override
    protected ColTag getThis() {
        return this;
    }

}
