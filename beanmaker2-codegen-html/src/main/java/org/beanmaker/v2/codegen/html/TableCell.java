package org.beanmaker.v2.codegen.html;

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

        Scope(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    public T abbr(String value) {
        return attribute("abbr", value);
    }

    public T axis(String value) {
        return attribute("axis", value);
    }

    public T headers(String value) {
        return attribute("headers", value);
    }

    public T scope(Scope value) {
        return attribute("scope", value.toString());
    }

    public T rowspan(int value) {
        return attribute("rowspan", Integer.toString(value));
    }

    public T colspan(int value) {
        return attribute("colspan", Integer.toString(value));
    }

}
