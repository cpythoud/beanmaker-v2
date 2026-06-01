package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class FormTag extends Tag<FormTag> {

    public enum EncodingType {
        URL_ENCODED("application/x-www-form-urlencoded"),
        MULTIPART("multipart/form-data"),
        PLAIN_TEXT("text/plain");

        private final String val;

        private EncodingType(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    public enum Method {
        POST("post"),
        GET("get");

        private final String val;

        private Method(final String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    public FormTag() {
        element = new XMLElement("form", false);
    }

    @Override
    protected FormTag getThis() {
        return this;
    }

    /*@Override
    public FormTag child(final Tag tag) {
        return super.child(tag);
    }

    @Override
    public FormTag addCodeFragment(final HtmlCodeFragment codeFragment) {
        return super.addCodeFragment(codeFragment);
    }*/

    public FormTag acceptCharset(final String value) {
        return attribute("accept-charset", value);
    }

    public FormTag name(final String value) {
        return attribute("name", value);
    }

    public FormTag autocomplete(final boolean on) {
        if (on)
            return attribute("autocomplete", "on");

        return attribute("autocomplete", "off");
    }

    public FormTag action(final String value) {
        return attribute("action", value);
    }

    public FormTag enctype(final EncodingType enc) {
        return attribute("enctype", enc.getVal());
    }

    public FormTag method(final Method method) {
        return attribute("method", method.getVal());
    }

    public FormTag novalidate() {
        return attribute("novalidate");
    }

    public FormTag target(final String value) {
        return attribute("target", value);
    }
}
