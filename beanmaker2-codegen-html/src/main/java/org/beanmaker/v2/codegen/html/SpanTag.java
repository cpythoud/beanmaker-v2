package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class SpanTag extends Tag<SpanTag> {

    public SpanTag() {
        element = new XMLElement("span", false);
    }

    public SpanTag(String body) {
        element = new XMLElement("span", body, false);
    }

    @Override
    protected SpanTag getThis() {
        return this;
    }

}
