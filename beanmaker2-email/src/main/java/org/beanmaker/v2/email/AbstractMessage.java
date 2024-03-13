package org.beanmaker.v2.email;

import java.util.List;

public abstract class AbstractMessage implements Message {

    protected List<Recipient> getRecipients(List<Recipient> recipients, AddressField field) {
        return recipients
                .stream()
                .filter(recipient -> recipient.getAddressField().equals(field))
                .toList();
    }

}
