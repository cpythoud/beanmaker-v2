package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TheadTag extends TableElement<TheadTag> {

    public TheadTag() {
        element = new XMLElement("thead", false);
    }

    @Override
    protected TheadTag getThis() {
        return this;
    }

    public TheadTag child(final TrTag tr) {
        return super.child(tr);
    }
}
