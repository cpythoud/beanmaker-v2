package org.beanmaker.v2.console;

import java.io.PrintStream;

public enum ConsoleType {
    DATA(System.out), MESSAGES(System.err);

    private final PrintStream out;

    ConsoleType(PrintStream out) {
        this.out = out;
    }

    PrintStream getPrintStream() {
        return out;
    }

}
