package org.beanmaker.v2.email;

import org.beanmaker.v2.util.Strings;

import java.util.Optional;

public class PlainRecipient implements Recipient {
    private final String email;
    private final AddressField field;
    private final String displayName;

    private PlainRecipient(String email, AddressField field, String displayName) {
        this.email = email;
        this.field = field;
        this.displayName = displayName;
    }

    public static PlainRecipient create(String email, AddressField field, String displayName) {
        return new PlainRecipient(email, field, displayName);
    }

    public static PlainRecipient create(String email, AddressField field) {
        return new PlainRecipient(email, field, null);
    }

    public static PlainRecipient to(String email, String displayName) {
        return create(email, AddressField.TO, displayName);
    }

    public static PlainRecipient to(String email) {
        return to(email, null);
    }

    public static PlainRecipient cc(String email, String displayName) {
        return create(email, AddressField.CC, displayName);
    }

    public static PlainRecipient cc(String email) {
        return cc(email, null);
    }

    public static PlainRecipient bcc(String email, String displayName) {
        return create(email, AddressField.BCC, displayName);
    }

    public static PlainRecipient bcc(String email) {
        return bcc(email, null);
    }

    public String getEmail() {
        return email;
    }

    public AddressField getAddressField() {
        return field;
    }

    public Optional<String> getDisplayName() {
        if (Strings.isEmpty(displayName))
            return Optional.empty();

        return Optional.of(displayName);
    }

}
