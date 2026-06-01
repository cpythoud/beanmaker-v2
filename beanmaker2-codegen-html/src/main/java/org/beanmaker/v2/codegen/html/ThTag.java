package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ThTag extends TableCell<ThTag> {

    public ThTag() {
        element = new XMLElement("th", false);
    }

    public ThTag(String body) {
        element = new XMLElement("th", body, false);
    }

    @Override
    protected ThTag getThis() {
        return this;
    }

}
