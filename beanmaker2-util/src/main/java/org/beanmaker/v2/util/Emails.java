package org.beanmaker.v2.util;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import java.util.Hashtable;

import java.util.regex.Pattern;

public class Emails {

    private static final Pattern EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        return EMAIL_ADDRESS_PATTERN.matcher(emailStr).find();
    }

    public static boolean hasAssociatedMxRecords(String emailOrDomain) {
        String domain = emailOrDomain.indexOf('@') == -1 ? emailOrDomain : emailOrDomain.split("@")[1];
        if (!Domains.validate(domain))
            return false;

        try {
            var env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
            var initialDirContext = new InitialDirContext(env);
            Attributes attributes;
            try {
                attributes = initialDirContext.getAttributes(domain, new String[] {"MX"});
            } catch (NameNotFoundException nnfex) {
                return false;
            }
            var attribute = attributes.get("MX");
            return attribute != null && attribute.size() > 0;
        } catch (NamingException nex) {
            throw new RuntimeException(nex);
        }
    }

}
