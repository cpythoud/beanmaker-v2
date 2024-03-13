package org.beanmaker.v2.email;

import org.beanmaker.v2.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlainMessageBuilder extends AbstractMessageBuilder {

    private boolean lenient = false;

    private Sender sender;
    private final List<Recipient> recipients = new ArrayList<>();
    private String textContent;
    private String htmlContent;
    private final List<FileAttachment> attachments = new ArrayList<>();
    private final List<EmbeddedImage> embeddedImages = new ArrayList<>();

    @Override
    public MessageBuilder setSender(Sender sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public MessageBuilder addRecipient(Recipient recipient) {
        if (lenient)
            addRecipientIfRequired(recipient);
        else
            checkAndAddUniqueRecipient(recipient);

        return this;
    }

    /**
     * We need to:
     * - add the recipients if it's nowhere to be found
     * - if it's already present at a higher level, ignore it
     * - if it's already present at the same level, ignore it
     * - if it's present at a lower, place it at the appropriate level and delete it from the lower level
     */
    private void addRecipientIfRequired(Recipient recipient) {
        getMatchingRecipient(recipients, recipient).ifPresentOrElse(
                matchingRecipient -> {
                    if (matchingRecipient.getAddressField().ordinal() > recipient.getAddressField().ordinal())
                        replaceRecipient(recipients, recipient);
                },
                () -> recipients.add(recipient)
        );
    }

    private void checkAndAddUniqueRecipient(Recipient recipient) {
        getMatchingRecipient(recipients, recipient).ifPresentOrElse(
                matchingRecipient -> {
                    throw new IllegalArgumentException(
                            "Recipient already added. Trying to add: " + recipient.getFormattedEmail()
                                    + ", already present: " + matchingRecipient.getFormattedEmail()
                    );
                },
                () -> recipients.add(recipient)
        );
    }

    @Override
    public MessageBuilder setTextContent(String content) {
        textContent = content;
        return this;
    }

    @Override
    public MessageBuilder setHTmlContent(String content) {
        htmlContent = content;
        return this;
    }

    @Override
    public MessageBuilder addFileAttachment(FileAttachment attachment) {
        if (fileAlreadyAttached(attachments, attachment)) {
            if (!lenient)
                throw new IllegalArgumentException("File attachment already added: " + attachment.getFileName());
        } else
            attachments.add(attachment);

        return this;
    }

    @Override
    public MessageBuilder addEmbeddedImage(EmbeddedImage image) {
        // TODO: add validation code
        embeddedImages.add(image);
        return this;
    }

    @Override
    public MessageBuilder lenient(boolean value) {
        lenient = value;
        return this;
    }

    @Override
    public Message build() {
        return new PlainMessage(
                sender,
                List.copyOf(recipients),
                textContent,
                htmlContent,
                List.copyOf(attachments),
                List.copyOf(embeddedImages)
        );
    }

    public boolean isLenient() {
        return lenient;
    }

    public Sender getSender() {
        return sender;
    }

    public List<Recipient> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    public Optional<String> getTextContent() {
        if (Strings.isEmpty(textContent))
            return Optional.empty();

        return Optional.of(textContent);
    }

    public Optional<String> getHtmlContent() {
        if (Strings.isEmpty(htmlContent))
            return Optional.empty();

        return Optional.of(htmlContent);
    }

    public List<FileAttachment> getFileAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public List<EmbeddedImage> getEmbeddedImages() {
        return Collections.unmodifiableList(embeddedImages);
    }

    public static class PlainMessage extends AbstractMessage {

        private final Sender sender;
        private final List<Recipient> recipients;
        private final String textContent;
        private final String htmlContent;
        private final List<FileAttachment> attachments;
        private final List<EmbeddedImage> embeddedImages;

        public PlainMessage(Sender sender, List<Recipient> recipients, String textContent, String htmlContent, List<FileAttachment> attachments, List<EmbeddedImage> embeddedImages) {
            this.sender = sender;
            this.recipients = recipients;
            this.textContent = textContent;
            this.htmlContent = htmlContent;
            this.attachments = attachments;
            this.embeddedImages = embeddedImages;
        }

        @Override
        public Sender getSender() {
            return sender;
        }

        @Override
        public List<Recipient> getRecipients(AddressField field) {
            return getRecipients(recipients, field);
        }

        @Override
        public Optional<String> getTextContent() {
            if (Strings.isEmpty(textContent))
                return Optional.empty();

            return Optional.of(textContent);
        }

        @Override
        public Optional<String> getHtmlContent() {
            if (Strings.isEmpty(htmlContent))
                return Optional.empty();

            return Optional.of(htmlContent);
        }

        @Override
        public List<FileAttachment> getFileAttachments() {
            return Collections.unmodifiableList(attachments);
        }

        @Override
        public List<EmbeddedImage> getEmbeddedImages() {
            return Collections.unmodifiableList(embeddedImages);
        }

    }

}
