package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class SectionTag extends Tag<SectionTag> {

    public SectionTag() {
        element = new XMLElement("section", false);
    }

    @Override
    protected SectionTag getThis() {
        return this;
    }
}
