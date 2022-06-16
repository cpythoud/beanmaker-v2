package org.beanmaker.v2.util.logging;

public enum Level {

    FATAL(1), ERROR(2),  WARNING(4), NOTICE(8),
    INFO(16), DEBUG(32), TRACE(64),  ALL(1024),
    OFF(0);

    private final int numericalLogLevel;

    Level(int numericalLogLevel) {
        this.numericalLogLevel = numericalLogLevel;
    }

    // TODO: implement Level from(java.util.logging.Level) static function
    // TODO: implement Level from(System.Logger.Level) static function

    public boolean equalOrBelow(Level level) {
        return numericalLogLevel <= level.numericalLogLevel;
    }

}
