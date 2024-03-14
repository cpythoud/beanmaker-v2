package org.beanmaker.v2.email.smtp;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;

import org.beanmaker.v2.email.AbstractMessageDispatcher;
import org.beanmaker.v2.email.FileAttachment;
import org.beanmaker.v2.email.Recipient;
import org.beanmaker.v2.email.Sender;

import java.util.List;

public abstract class AbstractSmtpServer extends AbstractMessageDispatcher {

    private static final String CHARSET = "UTF-8";

    protected EmailAttachment getEmailAttachment(FileAttachment fileAttachment) {
        var emailAttachment = new EmailAttachment();
        emailAttachment.setPath(fileAttachment.getFilePath().toAbsolutePath().toString());
        emailAttachment.setName(fileAttachment.getFileName());
        return emailAttachment;
    }

    protected void setFrom(Email email, Sender sender) throws EmailException {
        String name = sender.getDisplayName().orElse(null);
        if (name == null)
            email.setFrom(sender.getEmail());
        else
            email.setFrom(sender.getEmail(), name, CHARSET);
    }

    protected void addRecipients(Email email, List<Recipient> recipients) throws EmailException {
        for (var recipient: recipients) {
            String name = recipient.getDisplayName().orElse(null);
            if (name == null) {
                switch (recipient.getAddressField()) {
                    case TO:
                        email.addTo(recipient.getEmail());
                        break;
                    case CC:
                        email.addCc(recipient.getEmail());
                        break;
                    case BCC:
                        email.addBcc(recipient.getEmail());
                        break;
                }
            } else {
                switch (recipient.getAddressField()) {
                    case TO:
                        email.addTo(recipient.getEmail(), name, CHARSET);
                        break;
                    case CC:
                        email.addCc(recipient.getEmail(), name, CHARSET);
                        break;
                    case BCC:
                        email.addBcc(recipient.getEmail(), name, CHARSET);
                        break;
                }
            }
        }
    }

}
