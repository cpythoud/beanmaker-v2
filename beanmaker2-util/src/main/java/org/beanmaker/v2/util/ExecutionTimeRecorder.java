package org.beanmaker.v2.util;

public final class ExecutionTimeRecorder {

    private final long startTime;

    public ExecutionTimeRecorder() {
        startTime = System.nanoTime();
    }

    public long getSeconds() {
        return (System.nanoTime() - startTime) / 1_000_000_000;
    }

}
