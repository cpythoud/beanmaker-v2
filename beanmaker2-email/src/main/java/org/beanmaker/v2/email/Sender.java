package org.beanmaker.v2.email;

import java.util.Optional;

public interface Sender {

    String getEmail();

    Optional<String> getDisplayName();

    default String getFormattedEmail() {
        return getDisplayName()
                .map(displayName -> String.format("%s <%s>", displayName, getEmail()))
                .orElse(getEmail());
    }

}
