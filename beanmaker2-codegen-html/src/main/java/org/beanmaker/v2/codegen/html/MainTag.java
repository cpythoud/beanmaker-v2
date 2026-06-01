package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class MainTag extends Tag<MainTag> {

    public MainTag() {
        element = new XMLElement("main", false);
    }

    @Override
    protected MainTag getThis() {
        return this;
    }
}
