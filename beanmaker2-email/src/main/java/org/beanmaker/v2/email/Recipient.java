package org.beanmaker.v2.email;

import java.util.List;
import java.util.Optional;

public interface Recipient {

    static List<Recipient> getRecipients(List<Recipient> recipients, AddressField field) {
        return recipients
                .stream()
                .filter(recipient -> recipient.getAddressField().equals(field))
                .toList();
    }

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
