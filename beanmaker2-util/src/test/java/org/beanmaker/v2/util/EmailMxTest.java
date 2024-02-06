package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailMxTest {

    @Test
    void testDomains() {
        testDomainOK("abcis.ch");
        testDomainOK("moria.ch");
        testDomainNotOK("moria.local");  // * doesn't exist
        testDomainNotOK("alpharpg.com"); // * exists, but has no MX
    }

    private void testDomainOK(String domain) {
        String email = "local.part@" + domain;
        assertTrue(Domains.validate(domain));
        assertTrue(Emails.hasAssociatedMxRecords(email));
        assertTrue(Emails.hasAssociatedMxRecords(domain));
    }

    private void testDomainNotOK(String domain) {
        String email = "local.part@" + domain;
        assertTrue(Domains.validate(domain));
        assertFalse(Emails.hasAssociatedMxRecords(email));
        assertFalse(Emails.hasAssociatedMxRecords(domain));
    }

}
