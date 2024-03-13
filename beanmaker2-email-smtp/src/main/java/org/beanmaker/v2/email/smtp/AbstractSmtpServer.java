package org.beanmaker.v2.email.smtp;

import org.apache.commons.mail.EmailAttachment;
import org.beanmaker.v2.email.AbstractMessageDispatcher;
import org.beanmaker.v2.email.FileAttachment;

public abstract class AbstractSmtpServer extends AbstractMessageDispatcher {

    protected EmailAttachment getEmailAttachment(FileAttachment fileAttachment) {
        var emailAttachment = new EmailAttachment();
        emailAttachment.setPath(fileAttachment.getFilePath().toAbsolutePath().toString());
        emailAttachment.setName(fileAttachment.getFileName());
        return emailAttachment;
    }

}
