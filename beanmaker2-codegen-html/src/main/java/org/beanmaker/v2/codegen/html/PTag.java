package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class PTag extends Tag<PTag> {

    public PTag() {
        element = new XMLElement("p", false);
    }

    public PTag(String body) {
        element = new XMLElement("p", body, false);
    }

    @Override
    protected PTag getThis() {
        return this;
    }

}
