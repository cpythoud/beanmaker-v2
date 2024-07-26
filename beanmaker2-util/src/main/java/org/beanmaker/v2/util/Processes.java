package org.beanmaker.v2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes is a utility class that provides methods for executing external processes and retrieving their output.
 */
public class Processes {

    /**
     * Executes an external process and returns the output as a string.
     *
     * @param cmd The list of command-line arguments to be executed. Each element represents a separate argument.
     * @return The output of the external process as a string.
     * @throws RuntimeException If there is an I/O error while reading the output of the process.
     */
    public static String runExternalProcess(List<String> cmd) {
        var reader = new BufferedReader(new InputStreamReader(buildProcess(cmd).getInputStream()));
        String line;
        var output = new StringBuilder();
        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            output.append(line);
            output.append(System.lineSeparator());
        }
        return output.toString();
    }

    private static Process buildProcess(List<String> cmd) {
        var processBuilder = new ProcessBuilder(cmd);
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return process;
    }

    /**
     * Executes an external process and returns the output as a list of strings.
     *
     * @param cmd The list of command-line arguments to be executed. Each element represents a separate argument.
     * @return The output of the external process as a list of strings.
     * @throws RuntimeException If there is an I/O error while reading the output of the process.
     */
    public static List<String> runExternalProcessAndReturnList(List<String> cmd) {
        var reader = new BufferedReader(new InputStreamReader(buildProcess(cmd).getInputStream()));
        String line;
        var output = new ArrayList<String>();
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            output.add(line);
        }
        return output;
    }

}
