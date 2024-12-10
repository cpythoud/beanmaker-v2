package org.beanmaker.v2.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class SVG {

    public static final String SVG_MIME_TYPE = "image/svg+xml";

    // ! This and similar functions below it only work if the attributes to be removed are actually present !
    // TODO: fix function to ensure that the removed attributes are indeed part of the svg tag
    public static String removeWidthAndHeight(String svg) {
        return svg.replaceFirst("width=\"\\d+(\\.\\d+)?\"", "")
                .replaceFirst("height=\"\\d+(\\.\\d+)?\"", "");
    }

    public static String removeWidthAndHeight(Path path) throws IOException {
        return removeWidthAndHeight(java.nio.file.Files.readString(path));
    }

    public static String removeWidthAndHeight(File file) throws IOException {
        return removeWidthAndHeight(file.toPath());
    }

    public static void removeWidthAndHeight(Path path, Path targetFile) throws IOException {
        java.nio.file.Files.writeString(targetFile, removeWidthAndHeight(path));
    }

    public static void removeWidthAndHeight(File file, File targetFile) throws IOException {
        removeWidthAndHeight(file.toPath(), targetFile.toPath());
    }

    public static void removeWidthAndHeight(String svgFile, String targetFile) throws IOException {
        removeWidthAndHeight(Path.of(svgFile), Path.of(targetFile));
    }

    // ! This and similar functions below it only work if the attribute to be removed is actually present !
    // TODO: fix function to ensure that the removed attribute is indeed part of the svg tag
    public static String removeViewBox(String svg) {
        return svg.replaceFirst(
                "viewBox=\"\\d+(\\.\\d+)? \\d+(\\.\\d+)? \\d+(\\.\\d+)? \\d+(\\.\\d+)?\"",
                ""
        );
    }

    public static String removeViewBox(Path path) throws IOException {
        return removeViewBox(java.nio.file.Files.readString(path));
    }

    public static String removeViewBox(File file) throws IOException {
        return removeViewBox(file.toPath());
    }

    public static void removeViewBox(Path path, Path targetFile) throws IOException {
        java.nio.file.Files.writeString(targetFile, removeViewBox(path));
    }

    public static void removeViewBox(File file, File targetFile) throws IOException {
        removeViewBox(file.toPath(), targetFile.toPath());
    }

    public static void removeViewBox(String svgFile, String targetFile) throws IOException {
        removeViewBox(Path.of(svgFile), Path.of(targetFile));
    }

}
