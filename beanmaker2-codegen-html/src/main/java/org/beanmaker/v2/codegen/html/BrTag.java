package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class BrTag extends Tag<BrTag> {

    public BrTag() {
        element = new XMLElement("br");
    }

    @Override
    protected BrTag getThis() {
        throw new IllegalArgumentException("<br/> cannot be combined with attributes or child elements");
    }
}
