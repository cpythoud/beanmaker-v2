package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TrTag extends TableElement<TrTag> {

    public TrTag() {
        element = new XMLElement("tr", false);
    }

    @Override
    protected TrTag getThis() {
        return this;
    }

    public TrTag child(TableCell tableCell) {
        return super.child(tableCell);
    }

}
