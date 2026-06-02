package org.beanmaker.v2.codegen.html;

import org.beanmaker.v2.codegen.html.xmlbase.BooleanXMLAttribute;
import org.beanmaker.v2.codegen.html.xmlbase.XMLAttribute;
import org.beanmaker.v2.codegen.html.xmlbase.XMLElement;
import org.beanmaker.v2.codegen.html.xmlbase.ValueXMLAttribute;

import java.util.List;

/**
 * ...
 */
public abstract class Tag <T extends Tag<T>> {

    private int indentationLevel = 0;

    protected XMLElement element;

    protected abstract T getThis();


    public int getIndentationLevel() {
        return indentationLevel;
    }

    public void setIndentationLevel(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }


    public T id(String value) {
        return attribute("id", value);
    }

    public T changeId(String value) {
        if (element.hasAttribute("id")) {
            element.replaceAttribute("id", new ValueXMLAttribute("id", value));
            return getThis();
        }
        return id(value);
    }

    public T cssClass(String value) {
        return attribute("class", value);
    }

    public T changeCssClasses(String value) {
        if (element.hasAttribute("class")) {
            element.replaceAttribute("class", new ValueXMLAttribute("class", value));
            return getThis();
        }
        return cssClass(value);
    }

    public String getCssClasses() {
        XMLAttribute attribute = element.getAttribute("class");
        if (attribute == null)
            return "";

        return ((ValueXMLAttribute) attribute).value;
    }

    public T appendCssClasses(String value) {
        String currentClasses = getCssClasses();
        if (currentClasses.isBlank())
            return changeCssClasses(value);  // * In case of an empty class field, which would trigger an exception if using .cssClasses()

        return changeCssClasses(currentClasses + " " + value);
    }

    public T title(String value) {
        return attribute("title", value);
    }

    public T style(String value) {
        return attribute("style", value);
    }

    public T dir(String value) {
        return attribute("dir", value);
    }

    public T lang(String value) {
        return attribute("lang", value);
    }

    public T disabled() {
        return attribute("disabled");
    }

    public T tabindex(int value) {
        return attribute("tabindex", Integer.toString(value));
    }

    public T role(String value) {
        return attribute("role", value);
    }

    public T data(String name, String value) {
        return attribute("data-" + name, value);
    }

    public T attribute(String name) {
        element.addAttribute(new BooleanXMLAttribute(name));
        return getThis();
    }

    public T attribute(String name, String value) {
        element.addAttribute(new ValueXMLAttribute(name, value != null ? value : ""));
        return getThis();
    }

    public T singleQuotedAttribute(String name, String value) {
        element.addAttribute(new ValueXMLAttribute(name, value != null ? value : "", true));
        return getThis();
    }


    public T child(Tag<?> tag) {
        element.addChild(tag.element);
        return getThis();
    }

    public T addCodeFragment(HtmlCodeFragment codeFragment) {
        for (var tag: codeFragment.getTags())
            child(tag);
        return getThis();
    }


    @Override
    public String toString() {
        return element.toString(indentationLevel);
    }

    public String toString(int indentationLevel) {
        return element.toString(indentationLevel);
    }


    public String getOpeningTag() {
        return element.getOpeningTag();
    }

    public String getClosingTag() {
        return element.getClosingTag();
    }


    public void setOnOneLine(boolean onOneLine) {
        element.setOnOneLine(onOneLine);
    }

    public boolean isOnOneLine() {
        return element.isOnOneLine();
    }


    static <T extends Tag<?>> T createList(T listTag, List<Object> items) {
        for(Object item: items)
            listTag.child(new LiTag(item.toString()));

        return listTag;
    }

}
