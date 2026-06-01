package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

public class DlTag extends Tag<DlTag> {

    public DlTag() {
        element = new XMLElement("dl", false);
    }

    @Override
    protected DlTag getThis() {
        return this;
    }
}
