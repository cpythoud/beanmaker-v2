package org.beanmaker.v2.email;

public class PlainSender {

    private final String email;
    private final String displayName;

    private PlainSender(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public static PlainSender create(String email, String displayName) {
        return new PlainSender(email, displayName);
    }

    public static PlainSender create(String email) {
        return new PlainSender(email, "");
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

}
