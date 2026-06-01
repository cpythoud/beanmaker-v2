package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class KbdTag extends Tag<KbdTag> {

    public KbdTag() {
        element = new XMLElement("kbd", false);
    }

    public KbdTag(final String body) {
        element = new XMLElement("kbd", body, false);
    }

    @Override
    protected KbdTag getThis() {
        return this;
    }
}
