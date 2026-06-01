package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TeleTypewriterTag extends Tag<TeleTypewriterTag> {

    public TeleTypewriterTag() {
        element = new XMLElement("sup", false);
    }

    public TeleTypewriterTag(String body) {
        element = new XMLElement("sup", body, false);
    }

    @Override
    protected TeleTypewriterTag getThis() {
        return this;
    }

}
