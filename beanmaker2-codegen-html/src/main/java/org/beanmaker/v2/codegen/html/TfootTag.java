package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TfootTag extends TableElement<TfootTag> {

    public TfootTag() {
        element = new XMLElement("tfoot", false);
    }

    @Override
    protected TfootTag getThis() {
        return this;
    }

    public TfootTag child(final TrTag tr) {
        return super.child(tr);
    }
}
