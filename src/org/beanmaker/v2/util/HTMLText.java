package org.beanmaker.v2.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class HTMLText {

    /**
     * Returns a String where the following characters are escaped to their HTML entity equivalents:
     * &lt;, &gt;, ", ', \, &amp;.
     * @param text to be escaped.
     * @return escaped version of text.
     * @see #escapeEssentialHTMLtext(String)
     */
    public static String escapeHTMLtext(String text) {
        StringBuilder result = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(text);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '\\') {
                result.append("&#092;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    /**
     * Returns a String where the following characters are escaped to their HTML entity equivalents:
     * &lt;, &gt;, &amp;.
     * @param text to be escaped.
     * @return escaped version of text.
     * @see #escapeHTMLtext(String)
     */
    public static String escapeEssentialHTMLtext(String text) {
        StringBuilder result = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(text);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }
}
