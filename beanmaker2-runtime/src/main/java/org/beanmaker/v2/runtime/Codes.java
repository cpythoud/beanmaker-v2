package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.Strings;

public class Codes {

    public static final String STANDARD_CODE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789_-";

    public static void adjustCopyCode(DbBeanWithCode copy) {
        copy.setCode(copy.getCode() + "_copy");
        if (!copy.isCodeUnique()) {
            String baseCode = copy.getCode();
            int index = 2;
            do {
                copy.setCode(baseCode + index);
                ++index;
            } while (!copy.isCodeUnique());
        }
    }

    public static String createStandardizedCode(String source) {
        return Strings.replaceUnknownChars(Strings.removeAccents(source).toLowerCase(), STANDARD_CODE_CHARACTERS);
    }

}
