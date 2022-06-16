package org.beanmaker.v2.util.logging;

public interface Logger {

    default void log(String message) {
        info(message);
    }

    void log(Level level, String message);
    void log(Level level, String zone, String message);

    void setLevel(Level level);
    Level getLevel();

    default void fatal(String message) {
        log(Level.FATAL, message);
    }
    default void fatal(String zone, String message) {
        log(Level.FATAL, zone, message);
    }

    default void error(String message) {
        log(Level.ERROR, message);
    }
    default void error(String zone, String message) {
        log(Level.ERROR, zone, message);
    }

    default void warning(String message) {
        log(Level.WARNING, message);
    }
    default void warning(String zone, String message) {
        log(Level.WARNING, zone, message);
    }

    default void notice(String message) {
        log(Level.NOTICE, message);
    }
    default void notice(String zone, String message) {
        log(Level.NOTICE, zone, message);
    }

    default void info(String message) {
        log(Level.INFO, message);
    }
    default void info(String zone, String message) {
        log(Level.INFO, zone, message);
    }

    default void debug(String message) {
        log(Level.DEBUG, message);
    }
    default void debug(String zone, String message) {
        log(Level.DEBUG, zone, message);
    }

    default void trace(String message) {
        log(Level.TRACE, message);
    }
    default void trace(String zone, String message) {
        log(Level.TRACE, zone, message);
    }

}
