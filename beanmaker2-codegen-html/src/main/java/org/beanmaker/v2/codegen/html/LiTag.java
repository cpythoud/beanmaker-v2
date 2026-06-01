package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class LiTag extends Tag<LiTag> {

    public LiTag() {
        element = new XMLElement("li", false);
    }

    public LiTag(String body) {
        element = new XMLElement("li", body, false);
    }

    @Override
    protected LiTag getThis() {
        return this;
    }

    public LiTag value(int value) {  // * only if li is child of ol
        return attribute("value", Integer.toString(value));
    }
}
