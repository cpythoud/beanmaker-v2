package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ButtonTag extends FormElement<ButtonTag> {

    public enum ButtonType {
        SUBMIT("submit"),
        RESET("reset"),
        BUTTON("button"),
        MENU("button");

        private final String val;

        private ButtonType(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        public static ButtonType getType(final String s) {
            final String val = s.toLowerCase();
            if (val.equals("submit"))
                return SUBMIT;
            if (val.equals("reset"))
                return RESET;
            if (val.equals("button"))
                return BUTTON;
            if (val.equals("menu"))
                return MENU;

            throw new IllegalArgumentException(val + " does not correspond to a button type");
        }
    }

    public ButtonTag(final ButtonType type) {
        element = new XMLElement("button", false);
        type(type);
    }

    public ButtonTag(final ButtonType type, final String label) {
        element = new XMLElement("button", label, false);
        type(type);
    }

    @Override
    protected ButtonTag getThis() {
        return this;
    }

    /*@Override
    public ButtonTag child(final Tag tag) {
        return super.child(tag);
    }

    @Override
    public ButtonTag addCodeFragment(final HtmlCodeFragment codeFragment) {
        return super.addCodeFragment(codeFragment);
    }*/

    public ButtonTag type(final ButtonType value) {
        return attribute("type", value.getVal());
    }

    public ButtonTag menu(final String value) {
        return attribute("menu", value);
    }

    public ButtonTag name(final String value) {
        return attribute("name", value);
    }

    public ButtonTag value(final String value) {
        return attribute("value", value);
    }
}
