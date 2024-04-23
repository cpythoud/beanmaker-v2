package org.beanmaker.v2.email;

import org.beanmaker.v2.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PlainMessage implements Message {

    private final Sender sender;
    private final List<Recipient> recipients;
    private final String subject;
    private final String textContent;
    private final String htmlContent;
    private final List<FileAttachment> attachments;
    private final List<EmbeddedImageUrl> embeddedImageUrls;
    private final List<EmbeddedImageFile> embeddedImageFiles;

    private PlainMessage(
            Sender sender,
            List<Recipient> recipients,
            String subject,
            String textContent,
            String htmlContent,
            List<FileAttachment> attachments,
            List<EmbeddedImageUrl> embeddedImageUrls,
            List<EmbeddedImageFile> embeddedImageFiles)
    {
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.textContent = textContent;
        this.htmlContent = htmlContent;
        this.attachments = attachments;
        this.embeddedImageUrls = embeddedImageUrls;
        this.embeddedImageFiles = embeddedImageFiles;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Sender getSender() {
        return sender;
    }

    @Override
    public List<Recipient> getRecipients(AddressField field) {
        return Recipient.getRecipients(recipients, field);
    }

    @Override
    public List<Recipient> getAllRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    @Override
    public String getSubject() {
        return subject;
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
    public List<EmbeddedImageUrl> getEmbeddedImageUrls() {
        return Collections.unmodifiableList(embeddedImageUrls);
    }

    @Override
    public List<EmbeddedImageFile> getEmbeddedImageFiles() {
        return Collections.unmodifiableList(embeddedImageFiles);
    }

    public static class Builder extends AbstractMessageBuilder {

        private boolean lenient = false;

        private Sender sender;
        private final List<Recipient> recipients = new ArrayList<>();
        private String subject;
        private String textContent;
        private String htmlContent;
        private final List<FileAttachment> attachments = new ArrayList<>();
        private final List<EmbeddedImageUrl> embeddedImageUrls = new ArrayList<>();
        private final List<EmbeddedImageFile> embeddedImageFiles = new ArrayList<>();

        private Builder() { }

        @Override
        public MessageBuilder setSender(Sender sender) {
            Objects.requireNonNull(sender);
            this.sender = sender;
            return this;
        }

        @Override
        public MessageBuilder addRecipient(Recipient recipient) {
            Objects.requireNonNull(recipient);
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
        public MessageBuilder setSubject(String subject) {
            Objects.requireNonNull(subject);
            this.subject = subject;
            return this;
        }

        @Override
        public MessageBuilder setTextContent(String content) {
            Objects.requireNonNull(content);
            textContent = content;
            return this;
        }

        @Override
        public MessageBuilder setHtmlContent(String content) {
            Objects.requireNonNull(content);
            htmlContent = content;
            return this;
        }

        @Override
        public MessageBuilder addFileAttachment(FileAttachment attachment) {
            Objects.requireNonNull(attachment);
            if (fileAlreadyAttached(attachments, attachment)) {
                if (!lenient)
                    throw new IllegalArgumentException("File attachment already added: " + attachment.getFileName());
            } else
                attachments.add(attachment);

            return this;
        }

        @Override
        public MessageBuilder addEmbeddedImage(EmbeddedImageUrl image) {
            Objects.requireNonNull(image);
            if (imageReferenceAlreadyPresent(embeddedImageUrls, embeddedImageFiles, image))
                throw new IllegalArgumentException("There is already an image with name: " + image.getName());
            embeddedImageUrls.add(image);
            return this;
        }

        @Override
        public MessageBuilder addEmbeddedImage(EmbeddedImageFile image) {
            Objects.requireNonNull(image);
            if (imageReferenceAlreadyPresent(embeddedImageUrls, embeddedImageFiles, image))
                throw new IllegalArgumentException("There is already an image with name: " + image.getName());
            embeddedImageFiles.add(image);
            return this;
        }

        @Override
        public MessageBuilder lenient(boolean value) {
            lenient = value;
            return this;
        }

        @Override
        public Message build() {
            if (sender == null)
                throw new IllegalStateException("No sender specified");
            if (!hasToFieldRecipient(recipients))
                throw new IllegalStateException("No To: recipient has been added");
            if (Strings.isEmpty(subject))
                throw new IllegalStateException("Subject missing");
            if (Strings.isEmpty(textContent) && Strings.isEmpty(htmlContent))
                throw new IllegalStateException("Message has no content");
            if ((!embeddedImageUrls.isEmpty() || !embeddedImageFiles.isEmpty()) && Strings.isEmpty(htmlContent))
                throw new IllegalStateException("HTML content must be present to embed images");

            return new PlainMessage(
                    sender,
                    List.copyOf(recipients),
                    subject,
                    textContent,
                    htmlContent,
                    List.copyOf(attachments),
                    List.copyOf(embeddedImageUrls),
                    List.copyOf(embeddedImageFiles)
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

        public List<EmbeddedImageUrl> getEmbeddedImageUrls() {
            return Collections.unmodifiableList(embeddedImageUrls);
        }

        public List<EmbeddedImageFile> getEmbeddedImageFiles() {
            return Collections.unmodifiableList(embeddedImageFiles);
        }

    }

}
