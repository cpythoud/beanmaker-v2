package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class CaptionTag extends Tag<CaptionTag> {

    public CaptionTag(String caption) {
        element = new XMLElement("caption", caption, false);
    }

    @Override
    protected CaptionTag getThis() {
        return this;
    }

}
