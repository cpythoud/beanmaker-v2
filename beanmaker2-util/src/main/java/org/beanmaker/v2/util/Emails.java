package org.beanmaker.v2.util;

import java.util.regex.Pattern;

public class Emails {

    private static final Pattern EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        return EMAIL_ADDRESS_PATTERN.matcher(emailStr).find();
    }

}
