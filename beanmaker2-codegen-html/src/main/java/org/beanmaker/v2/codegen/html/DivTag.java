package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class DivTag extends Tag<DivTag> {

    public DivTag() {
        element = new XMLElement("div", false);
    }

    @Override
    protected DivTag getThis() {
        return this;
    }

}
