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

        private TableFrame(final String val) {
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

        private TableRules(final String val) {
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

    public TableTag summary(final String value) {
        return attribute("summary", value);
    }

    public TableTag width(final String value) {
        return attribute("width", value);
    }

    public TableTag border(final int value) {
        return attribute("width", Integer.toString(value));
    }

    public TableTag frame(final TableFrame value) {
        return attribute("width", value.getVal());
    }

    public TableTag rules(final TableRules value) {
        return attribute("width", value.getVal());
    }


    public TableTag child(final CaptionTag tag) {
        return super.child(tag);
    }

    public TableTag child(final ColElement tag) {
        return super.child(tag);
    }

    public TableTag child(final TableElement tag) {
        return super.child(tag);
    }
}
