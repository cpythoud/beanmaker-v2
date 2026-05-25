package org.beanmaker.v2.util;

import java.security.SecureRandom;

import java.util.Base64;

public final class SecurityTokenGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private SecurityTokenGenerator() { }

    public static String create() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

}
