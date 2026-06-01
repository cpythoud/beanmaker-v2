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

        ButtonType(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        public static ButtonType getType(String s) {
            String val = s.toLowerCase();
            return switch (val) {
                case "submit" -> SUBMIT;
                case "reset" -> RESET;
                case "button" -> BUTTON;
                case "menu" -> MENU;
                default -> throw new IllegalArgumentException(val + " does not correspond to a button type");
            };
        }
    }

    public ButtonTag(ButtonType type) {
        element = new XMLElement("button", false);
        type(type);
    }

    public ButtonTag(ButtonType type, String label) {
        element = new XMLElement("button", label, false);
        type(type);
    }

    @Override
    protected ButtonTag getThis() {
        return this;
    }

    public ButtonTag type(ButtonType value) {
        return attribute("type", value.getVal());
    }

    public ButtonTag menu(String value) {
        return attribute("menu", value);
    }

    public ButtonTag name(String value) {
        return attribute("name", value);
    }

    public ButtonTag value(String value) {
        return attribute("value", value);
    }

}
