package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class SubTag extends Tag<SubTag> {

    public SubTag() {
        element = new XMLElement("sub", false);
    }

    public SubTag(final String body) {
        element = new XMLElement("sub", body, false);
    }

    @Override
    protected SubTag getThis() {
        return this;
    }
}
