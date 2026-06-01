package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public abstract class TableCell<T extends TableCell<T>> extends TableElement<T> {

    public enum Scope {
        ROW("row"),
        COL("col"),
        ROWGROUP("rowgroup"),
        COLGROUP("colgroup");

        private final String val;

        private Scope(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    /*@Override
    public T child(final Tag tag) {
        return super.child(tag);
    }

    @Override
    public T addCodeFragment(final HtmlCodeFragment codeFragment) {
        return super.addCodeFragment(codeFragment);
    }*/

    public T abbr(final String value) {
        return attribute("abbr", value);
    }

    public T axis(final String value) {
        return attribute("axis", value);
    }

    public T headers(final String value) {
        return attribute("headers", value);
    }

    public T scope(final Scope value) {
        return attribute("scope", value.toString());
    }

    public T rowspan(final int value) {
        return attribute("rowspan", Integer.toString(value));
    }

    public T colspan(final int value) {
        return attribute("colspan", Integer.toString(value));
    }
}
