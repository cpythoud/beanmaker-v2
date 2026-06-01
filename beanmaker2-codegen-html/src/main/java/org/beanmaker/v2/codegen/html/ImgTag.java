package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ImgTag extends Tag<ImgTag> {

    public ImgTag() {
        element = new XMLElement("img");
    }

    public ImgTag(String src) {
        this();
        src(src);
    }

    public ImgTag(String src, String alt) {
        this(src);
        alt(alt);
    }

    public ImgTag(String src, String alt, int height, int width) {
        this(src, alt);
        height(height);
        width(width);
    }

    @Override
    protected ImgTag getThis() {
        return this;
    }

    public ImgTag src(String value) {
        return attribute("src", value);
    }

    public ImgTag alt(String value) {
        return attribute("alt", value);
    }

    public ImgTag longdesc(String value) {
        return attribute("longdesc", value);
    }

    public ImgTag name(String value) {
        return attribute("name", value);
    }

    public ImgTag height(int value) {
        return attribute("height", Integer.toString(value));
    }

    public ImgTag width(int value) {
        return attribute("width", Integer.toString(value));
    }

    public ImgTag usemap(String value) {
        return attribute("usemap", value);
    }

    public ImgTag ismap() {
        return attribute("ismap");
    }

}
