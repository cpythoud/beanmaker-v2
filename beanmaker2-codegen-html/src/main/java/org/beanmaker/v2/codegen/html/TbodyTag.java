package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TbodyTag extends TableElement<TbodyTag> {

    public TbodyTag() {
        element = new XMLElement("tbody", false);
    }

    @Override
    protected TbodyTag getThis() {
        return this;
    }

    public TbodyTag child(TrTag tr) {
        return super.child(tr);
    }

}
