package org.beanmaker.v2.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public class HTMLText {

    private static final String[] ENTITIES = {
            "&aacute;", "&agrave;", "&acirc;", "&auml;",
            "&eacute;", "&egrave;", "&ecirc;", "&euml;",
            "&iacute;", "&igrave;", "&icirc;", "&iuml;",
            "&oacute;", "&ograve;", "&ocirc;", "&ouml;",
            "&uacute;", "&ugrave;", "&ucirc;", "&uuml;",
            "&Aacute;", "&Agrave;", "&Acirc;", "&Auml;",
            "&Eacute;", "&Egrave;", "&Ecirc;", "&Euml;",
            "&Iacute;", "&Igrave;", "&Icirc;", "&Iuml;",
            "&Oacute;", "&Ograve;", "&Ocirc;", "&Ouml;",
            "&Uacute;", "&Ugrave;", "&Ucirc;", "&Uuml;",
            "&ccedil;", "&Ccedil;", "&yacute;", "&Yacute;",
            "&atilde;", "&ntilde;", "&otilde;",
            "&Atilde;", "&Ntilde;", "&Otilde;" };

    private static final char[] ACCENTED_CHARACTERS = {
            '\u00E1', '\u00E0', '\u00E2', '\u00E4',
            '\u00E9', '\u00E8', '\u00EA', '\u00EB',
            '\u00ED', '\u00EC', '\u00EE', '\u00EF',
            '\u00F3', '\u00F2', '\u00F4', '\u00F6',
            '\u00FA', '\u00F9', '\u00FB', '\u00FC',
            '\u00C1', '\u00C0', '\u00C2', '\u00C4',
            '\u00C9', '\u00C8', '\u00CA', '\u00CB',
            '\u00CD', '\u00CC', '\u00CE', '\u00CF',
            '\u00D3', '\u00D2', '\u00D4', '\u00D6',
            '\u00DA', '\u00D9', '\u00DB', '\u00DC',
            '\u00E7', '\u00C7', '\u00FD', '\u00DD',
            '\u00E3', '\u00F1', '\u00F5',
            '\u00C3', '\u00D1', '\u00D5' };

    private static final Map<Character, String> ACCENTED_CHARACTERS_TO_ENTITY_MAPPING;

    static {
        ACCENTED_CHARACTERS_TO_ENTITY_MAPPING = new HashMap<>();

        int index = 0;
        for (String entity: ENTITIES)
            ACCENTED_CHARACTERS_TO_ENTITY_MAPPING.put(ACCENTED_CHARACTERS[index++], entity);
    }

    public static String escapeAccents(String text) {
        var convertedText = new StringBuilder();
        var characterIterator = new StringCharacterIterator(text);

        for(char c = characterIterator.first(); c != CharacterIterator.DONE; c = characterIterator.next()) {
            String entity = ACCENTED_CHARACTERS_TO_ENTITY_MAPPING.get(c);
            if (entity != null)
                convertedText.append(entity);
            else
                convertedText.append(c);
        }

        return convertedText.toString();
    }

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
