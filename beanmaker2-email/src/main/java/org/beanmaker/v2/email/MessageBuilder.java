package org.beanmaker.v2.email;

public interface MessageBuilder {

    MessageBuilder setSender(Sender sender);

    MessageBuilder addRecipient(Recipient recipient);
    MessageBuilder setSubject(String subject);

    MessageBuilder setTextContent(String content);

    MessageBuilder setHtmlContent(String content);

    MessageBuilder addFileAttachment(FileAttachment attachment);

    MessageBuilder addEmbeddedImage(EmbeddedImageUrl image);
    MessageBuilder addEmbeddedImage(EmbeddedImageFile image);

    MessageBuilder lenient(boolean value);

    Message build();

}
