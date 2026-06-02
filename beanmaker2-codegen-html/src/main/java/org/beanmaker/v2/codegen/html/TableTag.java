package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class TableTag extends Tag<TableTag> {

    public enum TableFrame {
        VOID("void"),
        ABOVE("above"),
        BELOW("below"),
        HSIDES("hsides"),
        VSIDES("vsides"),
        LHS("lhs"),
        RHS("rhs"),
        BORDER("border");

        private final String val;

        TableFrame(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    public enum TableRules {
        NONE("none"),
        GROUPS("groups"),
        ROWS("rows"),
        COLS("cols"),
        ALL("all");

        private final String val;

        TableRules(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    public TableTag() {
        element = new XMLElement("table", false);
    }

    @Override
    protected TableTag getThis() {
        return this;
    }

    public TableTag summary(String value) {
        return attribute("summary", value);
    }

    public TableTag width(String value) {
        return attribute("width", value);
    }

    public TableTag border(int value) {
        return attribute("width", Integer.toString(value));
    }

    public TableTag frame(TableFrame value) {
        return attribute("width", value.getVal());
    }

    public TableTag rules(TableRules value) {
        return attribute("width", value.getVal());
    }


    public TableTag child(CaptionTag tag) {
        return super.child(tag);
    }

    public TableTag child(ColElement<?> tag) {
        return super.child(tag);
    }

    public TableTag child(TableElement<?> tag) {
        return super.child(tag);
    }

}
