package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ItalicTag extends Tag<ItalicTag> {

    public ItalicTag() {
        element = new XMLElement("i", false);
    }

    public ItalicTag(String body) {
        element = new XMLElement("i", body, false);
    }

    @Override
    protected ItalicTag getThis() {
        return this;
    }

}
