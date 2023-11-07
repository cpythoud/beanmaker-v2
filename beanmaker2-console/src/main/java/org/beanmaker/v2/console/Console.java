package org.beanmaker.v2.console;

import picocli.CommandLine;

import java.io.PrintStream;

public class Console {

    private final PrintStream out;

    private Status status;

    public Console(ConsoleType consoleType) {
        out = consoleType.getPrintStream();
    }

    public Console status(Status status) {
        this.status = status;
        return this;
    }

    public Console resetStatus() {
        status = null;
        return this;
    }

    public Console printStatus() {
        if (status == null || status.getPrefix() == null)
            return this;

        String statusText = "@|fg(%s),bold %s|@ ".formatted(status.getColor(), status.getPrefix());
        out.print(CommandLine.Help.Ansi.AUTO.string(statusText));
        return this;
    }

    public Console print(String text) {
        return print(text, null);
    }

    public Console print(String text, String extraStyles) {
        out.print(ansiFormat(text, extraStyles));
        return this;
    }

    public Console println(String text) {
        return println(text, null);
    }

    public Console println(String text, String extraStyles) {
        out.println(ansiFormat(text, extraStyles));
        return this;
    }

    private String ansiFormat(String text, String extraStyles) {
        if (status == null && extraStyles == null)
            return text;

        var ansiText = new StringBuilder();
        ansiText.append("@|");
        if (status != null)
            ansiText.append("fg(").append(status.getColor()).append("),bold");
        if (extraStyles != null) {
            if (status != null)
                ansiText.append(",");
            ansiText.append(extraStyles);
        }
        ansiText.append(" ").append(text).append("|@");

        return CommandLine.Help.Ansi.AUTO.string(ansiText.toString());
    }

    // * Quickies
    public void ok(String message) {
        status = Status.OK;
        println(message);
    }

    public void notice(String message) {
        status = Status.NOTICE;
        printStatus().println(message);
    }

    public void warning(String message) {
        status = Status.WARNING;
        printStatus().println(message);
    }

    public void error(String message) {
        status = Status.ERROR;
        printStatus().println(message);
    }

}
