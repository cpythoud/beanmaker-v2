package org.beanmaker.v2.util.logging;

public class SerrLogger extends AbstractLogger {

    public SerrLogger() {
        super();
    }

    public SerrLogger(Level level) {
        super(level);
    }

    @Override
    protected void printMessage(String composedMessage) {
        System.err.println(composedMessage);
    }

}
