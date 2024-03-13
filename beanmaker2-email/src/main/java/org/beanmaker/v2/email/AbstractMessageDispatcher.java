package org.beanmaker.v2.email;

public abstract class AbstractMessageDispatcher implements MessageDispatcher {

    protected boolean isTextOnly(Message message) {
        return message.getTextContent().isPresent() && message.getHtmlContent().isEmpty();
    }

    protected boolean isHtmlOnly(Message message) {
        return message.getHtmlContent().isPresent() && message.getTextContent().isEmpty();
    }

}
