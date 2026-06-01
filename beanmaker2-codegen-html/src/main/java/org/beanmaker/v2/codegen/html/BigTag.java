package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class BigTag extends Tag<BigTag> {

    public BigTag() {
        element = new XMLElement("big", false);
    }

    public BigTag(final String body) {
        element = new XMLElement("big", body, false);
    }

    @Override
    protected BigTag getThis() {
        return this;
    }
}
