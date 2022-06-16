package org.beanmaker.v2.util.logging;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DebugLogger extends AbstractLogger {

    public static final String DEFAULT_TEST_TIMESTAMP = "2020-01-01 11:22:33";

    private final Deque<String> messages = new ConcurrentLinkedDeque<>();

    private String timestamp = DEFAULT_TEST_TIMESTAMP;

    public DebugLogger() {
        super();
    }

    public DebugLogger(Level level) {
        super(level);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    protected void printMessage(String composedMessage) {
        messages.addFirst(composedMessage);
    }

    @Override
    protected String composeTimestamp() {
        return timestamp + " ";
    }

    public String popMessage() {
        if (messages.isEmpty())
            return null;

        return messages.removeFirst();
    }

    public String peekMessage() {
        if (messages.isEmpty())
            return null;

        return messages.getFirst();
    }

}
