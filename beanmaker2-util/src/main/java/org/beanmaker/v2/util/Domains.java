package org.beanmaker.v2.util;

import java.util.regex.Pattern;

public class Domains {

    private static final Pattern DOMAIN_PATTERN =
            Pattern.compile("^((?!-))(xn--)?[a-z0-9][a-z0-9-_]{0,61}[a-z0-9]?\\.(xn--)?([a-z0-9\\-]{1,61}|[a-z0-9-]{1,30}\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String domainStr) {
        return DOMAIN_PATTERN.matcher(domainStr).find();
    }

}
