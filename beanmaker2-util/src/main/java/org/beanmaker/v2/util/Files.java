package org.beanmaker.v2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import java.util.List;
import java.util.Scanner;

import java.util.stream.Collectors;

public class Files {

    /**
     * Checks if a file has an extension, as evidenced by a dot character in filename that is neither the first
     * nor the last filename character.
     * @param filename to check.
     * @return true if filename has an extension, false otherwise.
     * @see Files#hasExtension(File)
     */
    public static boolean hasExtension(String filename) {
        int lastDotPosition = filename.indexOf(".");

        if (lastDotPosition < 1)
            return false;

        String charBefore = filename.substring(lastDotPosition - 1, lastDotPosition);
        return !(charBefore.equals("/") || charBefore.equals("\\"));
    }

    /**
     * Checks if a file as an extension, as evidenced by a dot character in filename that is neither the first
     * nor the last filename character.
     * @param file to check.
     * @return true if the corresponding filename has an extension, false otherwise.
     * @see Files#hasExtension(String)
     */
    public static boolean hasExtension(File file) {
        return hasExtension(file.getName());
    }

    /**
     * Returns the extension of a filename.
     * @param filename which extension we are interested in.
     * @return filename's extension.
     * @throws java.lang.IllegalArgumentException if the filename has no extension
     * @see Files#hasExtension(String)
     * @see Files#getExtension(File)
     */
    public static String getExtension(String filename) {
        if (!hasExtension(filename))
            throw new IllegalArgumentException("File has no extension");

        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Returns the extension of a filename.
     * @param file which extension we are interested in.
     * @return file's extension.
     * @throws java.lang.IllegalArgumentException if the filename has no extension
     * @see Files#hasExtension(File)
     * @see Files#getExtension(String)
     */
    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    /**
     * Checks a filename extension against a list of acceptable extensions.
     * @param filename to be checked.
     * @param extensions, list of acceptable extensions.
     * @return true if filename has an extension and this extension match at least one of the allowed extensions.
     * @see Files#isFileExtensionOK(File, List)
     */
    public static boolean isFileExtensionOK(String filename, List<String> extensions) {
        if (!hasExtension(filename))
            return false;

        String extension = getExtension(filename).toLowerCase();

        return extensions.contains(extension);
    }

    /**
     * Checks a file's extension against a list of acceptable extensions.
     * @param file to be checked.
     * @param extensions, list of acceptable extensions.
     * @return true if the file has an extension and this extension match at least one of the allowed extensions.
     * @see Files#isFileExtensionOK(String, List)
     */
    public static boolean isFileExtensionOK(File file, List<String> extensions) {
        return isFileExtensionOK(file.getName(), extensions);
    }

    public static String removeFileExtension(String filename) {
        if (filename == null)
            throw new NullPointerException("Filename cannot be null");

        int index = filename.lastIndexOf(".");

        if (index == -1)
            return filename;  // * filename has no extension
        else
            return filename.substring(0, index);
    }

    /**
     * Replaces the extension of a given filename if it matches the specified old extension.
     *
     * @param filename the name of the file whose extension is to be replaced
     * @param oldExt the old extension that should be replaced
     * @param newExt the new extension to be applied to the filename
     * @return the filename with the new extension
     * @throws IllegalArgumentException if the filename does not have the specified old extension
     */
    public static String replaceExtension(String filename, String oldExt, String newExt) {
        if (filename.endsWith(oldExt)) {
            return filename.substring(0, filename.length() - oldExt.length()) + newExt;
        }

        throw new IllegalArgumentException("Filename " + filename + " does not have extension " + oldExt);
    }

    /**
     * Use a String to create an UTF-8 text file
     * @param s text to be written to file.
     * @param file into which the text is to be written
     * @see Files#write(String, File, String)
     */
    public static void write(String s, File file) {
        write(s, file, "UTF-8");
    }

    /**
     * Use a String to create an text file
     * @param s text to be written to file.
     * @param file into which the text is to be written
     * @param encoding of the resulting file
     * @see Files#write(String, File)
     */
    public static void write(String s, File file, String encoding) {
        try (
                var fos = new FileOutputStream(file);
                var osw = new OutputStreamWriter(fos, encoding);
                var out = new BufferedWriter(osw)
        ) {
            out.write(s);
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }
    }

    /**
     * Create an empty file or replace an existing file with an empty file
     * @param file to be created or made empty.
     */
    public static void createEmptyFile(File file) {
        if (file.exists() && !file.delete())
            throw new IllegalStateException("Could not delete preexisting file");

        try {
            if (!file.createNewFile())
                throw new IllegalStateException("Could not create file");
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }
    }

    public static void downloadFile(String fileURL, String destinationDir) throws IOException {
        downloadFile(fileURL, Path.of(destinationDir));
    }

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param destinationDir path of the directory to save the file
     * @throws IOException if something goes wrong
     */
    public static void downloadFile(String fileURL, Path destinationDir) throws IOException {
        var connection = (HttpURLConnection) new URL(fileURL).openConnection();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName;
            String dispositionHeader = connection.getHeaderField("Content-Disposition");

            if (dispositionHeader != null) {
                // * extracts file name from header field
                int index = dispositionHeader.indexOf("filename=");
                if (index != -1)
                    fileName = dispositionHeader.substring(index + 10, dispositionHeader.length() - 1);
                else
                    throw new IOException("Content-Disposition header does not contain filename information");
            } else {
                // * extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            }

            try (
                    var in = connection.getInputStream();
                    var out = new FileOutputStream(new File(destinationDir.toFile(), fileName))
            ) {
                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } else
            throw new IOException("File download failed. HTTP code: " + responseCode);

        connection.disconnect();
    }

    /**
     * Reads the content of a resource file using the specified file name
     * and returns it as a string.
     *
     * @param fileName the name of the resource file
     * @return the content of the resource file as a string
     */
    public static String readResourceFile(String fileName) {
        return readResourceFile(fileName, StandardCharsets.UTF_8);
    }

    /**
     * Reads the content of a resource file using the specified file name and encoding,
     * and returns it as a string.
     *
     * @param fileName  the name of the resource file
     * @param encoding  the character encoding of the resource file
     * @return the content of the resource file as a string
     * @throws IllegalArgumentException if the file is not found
     */
    public static String readResourceFile(String fileName, Charset encoding) {
        var inputStream = Files.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null)
            throw new IllegalArgumentException("File not found! " + fileName);

        try (var scanner = new Scanner(new InputStreamReader(inputStream, encoding)).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }


    /**
     * Reads the content of a resource file using the specified file name and returns it as a list of strings.
     *
     * @param fileName the name of the resource file
     * @return the content of the resource file as a list of strings
     * @throws IllegalArgumentException if the file is not found
     */
    public static List<String> readResourceFileToList(String fileName) {
        return readResourceFileToList(fileName, StandardCharsets.UTF_8);
    }

    /**
     * Reads the content of a resource file using the specified file name and returns it as a list of strings.
     *
     * @param fileName the name of the resource file
     * @param encoding the character encoding of the resource file
     * @return the content of the resource file as a list of strings
     * @throws IllegalArgumentException if the file is not found
     */
    public static List<String> readResourceFileToList(String fileName, Charset encoding) {
        var inputStream = Files.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null)
            throw new IllegalArgumentException("File not found! " + fileName);

        try (var isr = new InputStreamReader(inputStream, encoding);
            var reader = new BufferedReader(isr)) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + fileName, e);
        }
    }

}
