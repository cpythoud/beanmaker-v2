package org.beanmaker.v2.util.logging;

public class SoutLogger extends AbstractLogger {

    public SoutLogger() {
        super();
    }

    public SoutLogger(Level level) {
        super(level);
    }

    @Override
    protected void printMessage(String composedMessage) {
        System.out.println(composedMessage);
    }

}
