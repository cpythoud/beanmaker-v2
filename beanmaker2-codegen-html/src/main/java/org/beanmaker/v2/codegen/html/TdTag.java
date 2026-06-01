package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TdTag extends TableCell<TdTag> {

    public TdTag() {
        element = new XMLElement("td", false);
    }

    public TdTag(String body) {
        element = new XMLElement("td", body, false);
    }

    @Override
    protected TdTag getThis() {
        return this;
    }

}
