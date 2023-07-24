package org.beanmaker.v2.util;

import java.util.regex.Pattern;

/**
 * This class contain static functions to perform simple e-mail format validation.
 * It is sufficient for most server side validation of e-mail addresses in web applications.
 * This class DOES NOT attempt to be RFC compliant, but rather try to do a good job with the most common
 * e-mail address formats.
 * Restrictions: the legitimate e-mail addresses with the following characteristics will not validate using
 * this class: any e-mail comprising unicode characters, any e-mail with quoted or escaped characters.
 */
@Deprecated
public class EmailValidator {

    /**
     * Validate e-mail address.
     * @param email address to be validated.
     * @return true if the e-mail address validates, false otherwise.
     */
    public static boolean validate(String email) {
        return validate(email, false);
    }

    /**
     * Validate e-mail address.
     * @param email address to be validated.
     * @param allowIpForDomain if true authorize IPv4 addresses in lieu of domain name (not recommended).
     * @return true if the e-mail address validates, false otherwise.
     */
    public static boolean validate(String email, boolean allowIpForDomain) {
        return validate(email, allowIpForDomain, false);
    }

    /**
     * Validate e-mail address.
     * @param email address to be validated.
     * @param allowIpForDomain if true authorize IPv4 addresses in lieu of domain name (not recommended).
     * @param extensiveTldValidation (has no effect anymore, kept for backward compatibility, when maintaining
     *                               a list of current tlds was actually doable before ICANN went crazy).
     * @return true if the e-mail address validates, false otherwise.
     */
    public static boolean validate(String email, boolean allowIpForDomain, boolean extensiveTldValidation) {
        String[] parts = email.split("@");

        if (parts.length != 2)
            return false;

        String local = parts[0];
        String domain = parts[1];

        if (!dotsOK(local) || !dotsOK(domain))
            return false;

        String[] localParts = local.split("\\.");
        for (String part: localParts)
            if (!ALLOWED_CHARS_IN_LOCAL_PART.matcher(part).matches())
                return false;

        if (allowIpForDomain && IPv4Address.isValid(domain))
            return true;

        String[] domainParts = domain.split("\\.");
        if (domainParts.length < 2)
            return false;
        for (String part: domainParts)
            if (!ALLOWED_CHARS_IN_DOMAIN_PART.matcher(part).matches())
                return false;

        return true;
    }

    /**
     * Function used to check local and domain parts of e-mail address. The function makes sure the part doesn't
     * start and end with a dot, or that two consecutive dots are present.
     * @param part to be checked.
     * @return true if the part checks OK against the above mentioned criteria, false otherwise.
     */
    public static boolean dotsOK(String part) {
        return !part.startsWith(".") && !part.endsWith(".") && !part.contains("..");
    }

    /**
     * Regex pattern for characters allowed in local part.
     */
    public static final Pattern ALLOWED_CHARS_IN_LOCAL_PART = Pattern.compile("[A-Za-z0-9!#$%&'*+/=?^_`{|}~\\-]+");

    /**
     * Regex pattern for characters allowed in domain part.
     */
    public static final Pattern ALLOWED_CHARS_IN_DOMAIN_PART = Pattern.compile("[A-Za-z0-9\\-]+");
}
