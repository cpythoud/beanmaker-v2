package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class HeaderTag extends Tag<HeaderTag> {

    public HeaderTag(int level, String title) {
        if (level < 1 || level > 6)
            throw new IllegalArgumentException("In Hx header tags, we must have 1 <= x <= 6.");

        element = new XMLElement("h" + level, title, false);
    }

    @Override
    protected HeaderTag getThis() {
        return this;
    }

}
