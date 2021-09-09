package org.beanmaker.v2.util;

import java.io.File;

import java.util.List;

public class Files {

    /**
     * Checks if a file as an extension, as evidenced by a dot character in filename that is neither the first
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

        return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
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

}
