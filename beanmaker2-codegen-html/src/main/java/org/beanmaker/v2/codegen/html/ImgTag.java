package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;

/**
 * ...
 */
public class ImgTag extends Tag<ImgTag> {

    public ImgTag() {
        element = new XMLElement("img");
    }

    public ImgTag(final String src) {
        this();
        src(src);
    }

    public ImgTag(final String src, final String alt) {
        this(src);
        alt(alt);
    }

    public ImgTag(final String src, final String alt, final int height, final int width) {
        this(src, alt);
        height(height);
        width(width);
    }

    @Override
    protected ImgTag getThis() {
        return this;
    }

    public ImgTag src(final String value) {
        return attribute("src", value);
    }

    public ImgTag alt(final String value) {
        return attribute("alt", value);
    }

    public ImgTag longdesc(final String value) {
        return attribute("longdesc", value);
    }

    public ImgTag name(final String value) {
        return attribute("name", value);
    }

    public ImgTag height(final int value) {
        return attribute("height", Integer.toString(value));
    }

    public ImgTag width(final int value) {
        return attribute("width", Integer.toString(value));
    }

    public ImgTag usemap(final String value) {
        return attribute("usemap", value);
    }

    public ImgTag ismap() {
        return attribute("ismap");
    }
}
