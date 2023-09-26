package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

public class EmailManglerTest {

    @Test
    void testMangling() {
        var me1 = new MangledEmail.Builder("info@abcis.ch", "emm001")
                .setLinkText("Prenez contact avec nous")
                .setCssClass("email")
                .build();
        var me2 = new MangledEmail.Builder("info@abcis.ch", "emm002")
                .setCssClass("email")
                .build();
        System.out.println(me1.getJavascriptFunction());
        System.out.println(me2.getJavascriptFunction());
    }

    // TODO: test parameters to MangledEmail

}
