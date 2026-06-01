package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class AcronymTag extends Tag<AcronymTag> {

    public AcronymTag() {
        element = new XMLElement("acronym", false);
    }

    public AcronymTag(String body) {
        element = new XMLElement("acronym", body, false);
    }

    @Override
    protected AcronymTag getThis() {
        return this;
    }

}
