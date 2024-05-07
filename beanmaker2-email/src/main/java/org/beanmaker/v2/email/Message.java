package org.beanmaker.v2.email;

import java.util.List;
import java.util.Optional;

public interface Message {

    Sender getSender();

    List<Recipient> getRecipients(AddressField field);
    List<Recipient> getAllRecipients();

    String getSubject();

    Optional<String> getTextContent();
    Optional<String> getHtmlContent();

    List<FileAttachment> getFileAttachments();

    List<EmbeddedImageUrl> getEmbeddedImageUrls();
    List<EmbeddedImageFile> getEmbeddedImageFiles();

    default boolean hasAttachments() {
        return !getFileAttachments().isEmpty();
    }

    default boolean hasEmbeddedImages() {
        return !getEmbeddedImageUrls().isEmpty() || !getEmbeddedImageFiles().isEmpty();
    }

    default String debugString() {
        var debugString = new StringBuilder();

        debugString.append("From: ").append(getSender().getFormattedEmail()).append("\n");
        for (AddressField field : AddressField.values()) {
            List<Recipient> recipients = getRecipients(field);
            if (!recipients.isEmpty()) {
                debugString.append(field.displayName());
                for (Recipient recipient : recipients) {
                    debugString.append(recipient.getFormattedEmail()).append(", ");
                }
            }
            debugString.delete(debugString.length() - 2, debugString.length());
            debugString.append("\n");
        }
        debugString.append("Subject: ").append(getSubject()).append("\n\n");

        getTextContent().ifPresent(text -> debugString.append("TEXT CONTENT:\n").append(text).append("\n\n"));
        getHtmlContent().ifPresent(text -> debugString.append("HTML CONTENT:\n").append(text).append("\n\n"));

        List<FileAttachment> fileAttachments = getFileAttachments();
        if (!fileAttachments.isEmpty()) {
            debugString.append("FILE ATTACHMENTS:\n");
            for (FileAttachment attachment : fileAttachments) {
                debugString.append("File Name: ").append(attachment.getFileName()).append("\n");
                debugString.append("Mime Type: ").append(attachment.getMimeType()).append("\n");
                debugString.append("File Path: ").append(attachment.getFilePath()).append("\n\n");
            }
        }

        List<EmbeddedImageUrl> embeddedImageUrls = getEmbeddedImageUrls();
        if (!embeddedImageUrls.isEmpty()) {
            debugString.append("EMBEDDED IMAGE URLS:\n");
            for (EmbeddedImageUrl imageUrl : embeddedImageUrls) {
                debugString.append("Image Name: ").append(imageUrl.getName()).append("\n");
                debugString.append("Image URL: ").append(imageUrl.getImageUrl()).append("\n\n");
            }
        }

        List<EmbeddedImageFile> embeddedImageFiles = getEmbeddedImageFiles();
        if (!embeddedImageFiles.isEmpty()) {
            debugString.append("EMBEDDED IMAGE FILES:\n");
            for (EmbeddedImageFile imageFile : embeddedImageFiles) {
                debugString.append("Image Name: ").append(imageFile.getName()).append("\n");
                debugString.append("File Path: ").append(imageFile.getFilePath()).append("\n");
                debugString.append("Base64 Content: ").append(imageFile.getBase64Content()).append("\n\n");
            }
        }

        return debugString.toString();
    }

}
