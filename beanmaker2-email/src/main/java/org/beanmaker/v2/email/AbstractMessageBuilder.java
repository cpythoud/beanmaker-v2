package org.beanmaker.v2.email;

import java.io.IOException;

import java.nio.file.Files;

import java.util.List;
import java.util.Optional;

public abstract class AbstractMessageBuilder implements MessageBuilder {

    protected Optional<Recipient> getMatchingRecipient(List<Recipient> recipients, Recipient recipient) {
        for (var rcp: recipients)
            if (rcp.getEmail().equals(recipient.getEmail()))
                return Optional.of(rcp);

        return Optional.empty();
    }

    protected void replaceRecipient(List<Recipient> recipients, Recipient recipient) {
        int index = getIndex(recipients, recipient);
        if (index == -1)
            throw new IllegalStateException("Recipient cannot be found to be replaced: " + recipient.printDetails());

        recipients.remove(index);
        recipients.add(recipient);
    }

    protected int getIndex(List<Recipient> recipients, Recipient recipient) {
        int index = 0;
        for (var rcp: recipients) {
            if (rcp.getEmail().equals(recipient.getEmail()))
                return index;
            ++index;
        }

        return -1;
    }

    protected boolean hasToFieldRecipient(List<Recipient> recipients) {
        for (var recipient : recipients) {
            if (recipient.getAddressField() == AddressField.TO) {
                return true;
            }
        }
        return false;
    }

    protected boolean fileAlreadyAttached(List<FileAttachment> attachments, FileAttachment attachment) {
        try {
            for (var att : attachments)
                if (Files.isSameFile(att.getFilePath(), attachment.getFilePath()))
                    return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

}
