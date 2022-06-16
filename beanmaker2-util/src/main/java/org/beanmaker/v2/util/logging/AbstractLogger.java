package org.beanmaker.v2.util.logging;

import org.beanmaker.v2.util.Dates;
import org.beanmaker.v2.util.Strings;

public abstract class AbstractLogger implements Logger {

    private Level level;

    public AbstractLogger() {
        level = Level.ALL;
    }

    public AbstractLogger(Level level) {
        this.level = level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void log(Level level, String message) {
        log(level, null, message);
    }

    @Override
    public void log(Level level, String zone, String message) {
        if (level.equalOrBelow(this.level))
            printMessage(composeMessage(level, zone, message));
    }

    protected abstract void printMessage(String composedMessage);

    protected String composeMessage(Level level, String zone, String message) {
        return composeLevel(level) + composeZone(zone) + composeTimestamp() + message;
    }

    protected String composeLevel(Level level) {
        return "[" + level.toString() + "] ";
    }

    protected String composeZone(String zone) {
        if (Strings.isEmpty(zone))
            return "";

        return "--" + zone + "-- ";
    }

    protected String composeTimestamp() {
        return Dates.getCurrentTimestamp().toString().substring(0, 19) + " ";
    }

}
