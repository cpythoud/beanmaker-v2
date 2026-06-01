package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class EmTag extends Tag<EmTag> {

    public EmTag() {
        element = new XMLElement("em", false);
    }

    public EmTag(String body) {
        element = new XMLElement("em", body, false);
    }

    @Override
    protected EmTag getThis() {
        return this;
    }

}
