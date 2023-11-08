package org.beanmaker.v2.console;

import picocli.CommandLine;

public enum ReturnCode {
    SUCCESS(CommandLine.ExitCode.OK),
    SYSTEM_ERROR(CommandLine.ExitCode.SOFTWARE),
    USER_ERROR(CommandLine.ExitCode.USAGE);

    ReturnCode(int exitCode) {
        this.exitCode = exitCode;
    }

    private final int exitCode;

    public int code() {
        return exitCode;
    }

}
