package org.beanmaker.v2.email;

import java.util.List;
import java.util.Optional;

public interface Message {

    Sender getSender();

    List<Recipient> getRecipients(AddressField field);

    Optional<String> getTextContent();
    Optional<String> getHtmlContent();

    List<FileAttachment> getFileAttachments();

    List<EmbeddedImage> getEmbeddedImages();

}
