package org.beanmaker.v2.email;

public interface MessageBuilder {

    MessageBuilder setSender(Sender sender);

    MessageBuilder addRecipient(Recipient recipient);
    MessageBuilder setSubject(String subject);

    MessageBuilder setTextContent(String content);

    MessageBuilder setHTmlContent(String content);

    MessageBuilder addFileAttachment(FileAttachment attachment);

    MessageBuilder addEmbeddedImage(EmbeddedImage image);

    MessageBuilder lenient(boolean value);

    Message build();

}
