package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

public class NavTag extends Tag<NavTag> {

    public NavTag() {
        element = new XMLElement("nav", false);
    }

    @Override
    protected NavTag getThis() {
        return this;
    }

}
