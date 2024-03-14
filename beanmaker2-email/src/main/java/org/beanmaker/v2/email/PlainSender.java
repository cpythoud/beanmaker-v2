package org.beanmaker.v2.email;

import org.beanmaker.v2.util.Strings;

import java.util.Optional;

public class PlainSender implements Sender {

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

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Optional<String> getDisplayName() {
        if (Strings.isEmpty(displayName))
            return Optional.empty();

        return Optional.of(displayName);
    }

}
