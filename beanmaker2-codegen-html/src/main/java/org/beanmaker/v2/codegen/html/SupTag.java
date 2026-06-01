package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class SupTag extends Tag<SupTag> {

    public SupTag() {
        element = new XMLElement("sup", false);
    }

    public SupTag(String body) {
        element = new XMLElement("sup", body, false);
    }

    @Override
    protected SupTag getThis() {
        return this;
    }

}
