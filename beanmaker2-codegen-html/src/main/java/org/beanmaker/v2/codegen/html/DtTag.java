package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

public class DtTag extends Tag<DtTag> {

    public DtTag() {
        element = new XMLElement("dt", false);
    }

    public DtTag(String body) {
        element = new XMLElement("dt", body, false);
    }

    @Override
    protected DtTag getThis() {
        return this;
    }
}
