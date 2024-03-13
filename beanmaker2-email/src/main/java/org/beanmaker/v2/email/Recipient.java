package org.beanmaker.v2.email;

import java.util.Optional;

public interface Recipient {

    String getEmail();

    AddressField getAddressField();

    Optional<String> getDisplayName();

    default String getFormattedEmail() {
        return getDisplayName()
                .map(displayName -> String.format("%s <%s>", displayName, getEmail()))
                .orElse(getEmail());
    }

    default String printDetails() {
        return getAddressField().displayName() + getFormattedEmail();
    }

}
