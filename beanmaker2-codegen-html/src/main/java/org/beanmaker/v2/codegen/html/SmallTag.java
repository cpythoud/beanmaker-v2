package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class SmallTag extends Tag<SmallTag> {

    public SmallTag() {
        element = new XMLElement("small", false);
    }

    public SmallTag(final String body) {
        element = new XMLElement("small", body, false);
    }

    @Override
    protected SmallTag getThis() {
        return this;
    }
}
