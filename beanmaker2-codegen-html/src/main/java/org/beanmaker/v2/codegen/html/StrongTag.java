package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class StrongTag extends Tag<StrongTag> {

    public StrongTag() {
        element = new XMLElement("strong", false);
    }

    public StrongTag(String body) {
        element = new XMLElement("strong", body, false);
    }

    @Override
    protected StrongTag getThis() {
        return this;
    }

}
