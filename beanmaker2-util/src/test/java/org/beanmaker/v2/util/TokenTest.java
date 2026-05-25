package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

public class TokenTest {

    @Test
    void printToken() {
        for (int i = 0; i < 10; ++i)
            System.out.println(SecurityTokenGenerator.create());
    }

}
