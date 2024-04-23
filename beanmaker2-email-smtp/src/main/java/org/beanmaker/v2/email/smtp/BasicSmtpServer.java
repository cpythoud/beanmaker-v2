package org.beanmaker.v2.email.smtp;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import org.beanmaker.v2.email.Message;
import org.beanmaker.v2.email.MessageDispatcher;
import org.beanmaker.v2.email.MessageDispatcherBuilder;
import org.beanmaker.v2.email.Recipient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicSmtpServer extends AbstractSmtpServer {

    private final String smtpServer;
    private final int port;
    private final boolean useSSL;
    private final boolean useTLS;
    private final String smtpUser;
    private final String smtpPassword;

    private final List<Recipient> debugRecipients;

    private BasicSmtpServer(
            String smtpServer,
            int port,
            boolean useSSL,
            boolean useTLS,
            String smtpUser,
            String smtpPassword,
            List<Recipient> debugRecipients)
    {
        this.smtpServer = smtpServer;
        this.port = port;
        this.useSSL = useSSL;
        this.useTLS = useTLS;
        this.smtpUser = smtpUser;
        this.smtpPassword = smtpPassword;
        this.debugRecipients = debugRecipients;
    }

    public static Builder builder(String smtpServer) {
        return new Builder(smtpServer);
    }

    @Override
    public void send(Message message) {
        try {
            var apacheEmail = createEmail(message);

            apacheEmail.setHostName(smtpServer);
            apacheEmail.setSmtpPort(port);

            if (smtpUser != null)
                apacheEmail.setAuthenticator(new DefaultAuthenticator(smtpUser, smtpPassword));

            if (useTLS) {
                apacheEmail.setStartTLSEnabled(true);
                apacheEmail.setStartTLSRequired(true);
            } else if (useSSL)
                apacheEmail.setSSLOnConnect(true);

            setFrom(apacheEmail, message.getSender());
            if (debugRecipients == null || debugRecipients.isEmpty())
                addRecipients(apacheEmail, message.getAllRecipients());
            else
                addRecipients(apacheEmail, debugRecipients);

            apacheEmail.setSubject(message.getSubject());

            apacheEmail.send();
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    private Email createEmail(Message message) throws EmailException {
        if (isTextOnly(message)) {
            if (!message.hasAttachments())
                return createPlainTextEmail(message);

            return createPlainTextEmailWithAttachments(message);
        }

        return createHtmlEmail(message);
    }

    private Email createPlainTextEmail(Message message) throws EmailException {
        var simpleEmail = new SimpleEmail();

        simpleEmail.setMsg(message.getTextContent().orElseThrow());

        return simpleEmail;
    }

    private Email createPlainTextEmailWithAttachments(Message message) throws EmailException {
        var multiPartEmail = new MultiPartEmail();

        multiPartEmail.setMsg(message.getTextContent().orElseThrow());
        for (var attachment: message.getFileAttachments())
            multiPartEmail.attach(getEmailAttachment(attachment));

        return multiPartEmail;
    }

    private Email createHtmlEmail(Message message) throws EmailException {
        var htmlEmail = new HtmlEmail();

        String htmlContent = insertEmbeddedImages(message, htmlEmail);
        htmlEmail.setHtmlMsg(htmlContent);
        htmlEmail.setCharset("UTF-8");

        String textContent = message.getTextContent().orElse(null);
        if (textContent != null)
            htmlEmail.setTextMsg(textContent);

        for (var attachment : message.getFileAttachments())
            htmlEmail.attach(getEmailAttachment(attachment));

        return htmlEmail;
    }

    private String insertEmbeddedImages(Message message, HtmlEmail email) throws EmailException {
        var cids = getEmbeddedImageCids(message, email);

        String htmlContent = message.getHtmlContent().orElseThrow();
        for (String name: cids.keySet())
            htmlContent = htmlContent.replaceAll("==CID'" + name + "'==", cids.get(name));

        return htmlContent;
    }

    private Map<String, String> getEmbeddedImageCids(Message message, HtmlEmail email) throws EmailException {
        var cids = new HashMap<String, String>();

        for (var image: message.getEmbeddedImageUrls())
            cids.put(image.getName(), email.embed(image.getImageUrl(), image.getName()));

        for (var image : message.getEmbeddedImageFiles())
            cids.put(image.getName(), email.embed(image.getFilePath().toFile(), image.getName()));

        return cids;
    }

    public static class Builder implements MessageDispatcherBuilder {

        private final String smtpServer;
        private int port = 25;
        private boolean useSSL = false;
        private boolean useTLS = false;
        private String smtpUser;
        private String smtpPassword;

        private final List<Recipient> debugRecipients = new ArrayList<>();

        private Builder(String smtpServer) {
            this.smtpServer = smtpServer;
        }

        public Builder port(int port) {
            if (port < 0 || port > 65535)
                throw new IllegalArgumentException("Port must be in the allowed range: 0-65535");

            this.port = port;
            return this;
        }

        public Builder encryptionType(EncryptionType encryptionType) {
            switch (encryptionType) {
                case NONE:
                    useSSL = false;
                    useTLS = false;
                    break;
                case SSL:
                    useSSL = true;
                    useTLS = false;
                    break;
                case TLS:
                    useSSL = false;
                    useTLS = true;
                    break;
            }
            return this;
        }

        public Builder smtpCredentials(String user, String password) {
            smtpUser = user;
            smtpPassword = password;
            return this;
        }

        public Builder debugRecipients(List<Recipient> recipients) {
            debugRecipients.clear();
            debugRecipients.addAll(recipients);
            return this;
        }

        @Override
        public MessageDispatcher build() {
            return new BasicSmtpServer(smtpServer, port, useSSL, useTLS, smtpUser, smtpPassword, debugRecipients);
        }

        public String getSmtpServer() {
            return smtpServer;
        }

        public int getPort() {
            return port;
        }

        public boolean useSSL() {
            return useSSL;
        }

        public boolean useTLS() {
            return useTLS;
        }

        public String getSmtpUser() {
            return smtpUser;
        }

        public String getSmtpPassword() {
            return smtpPassword;
        }

        public List<Recipient> getDebugRecipients() {
            return Collections.unmodifiableList(debugRecipients);
        }
    }

}
