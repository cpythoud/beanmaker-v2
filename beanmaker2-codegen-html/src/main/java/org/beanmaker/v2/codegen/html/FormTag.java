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

        EncodingType(String val) {
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

        Method(String val) {
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

    public FormTag acceptCharset(String value) {
        return attribute("accept-charset", value);
    }

    public FormTag name(String value) {
        return attribute("name", value);
    }

    public FormTag autocomplete(boolean on) {
        if (on)
            return attribute("autocomplete", "on");

        return attribute("autocomplete", "off");
    }

    public FormTag action(String value) {
        return attribute("action", value);
    }

    public FormTag enctype(EncodingType enc) {
        return attribute("enctype", enc.getVal());
    }

    public FormTag method(Method method) {
        return attribute("method", method.getVal());
    }

    public FormTag novalidate() {
        return attribute("novalidate");
    }

    public FormTag target(String value) {
        return attribute("target", value);
    }

}
