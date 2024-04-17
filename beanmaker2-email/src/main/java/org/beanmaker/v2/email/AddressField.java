package org.beanmaker.v2.email;

public enum AddressField {
    // ! The order of the fields is important and should never be changed
    TO("To: "), CC("Cc: "), BCC("Bcc: ");

    private final String displayName;

    AddressField(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

}
