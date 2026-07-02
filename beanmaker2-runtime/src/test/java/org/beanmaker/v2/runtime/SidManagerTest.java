package org.beanmaker.v2.runtime;

import org.junit.jupiter.api.Test;

public class SidManagerTest {

    @Test
    void printSid() {
        for (int i = 0; i < 10; ++i)
            System.out.println(SidManager.createSid());
    }

}
