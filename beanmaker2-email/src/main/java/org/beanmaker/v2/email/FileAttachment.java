package org.beanmaker.v2.email;

import java.io.File;
import java.nio.file.Path;

public class FileAttachment {

    private final Path filePath;
    private final String fileName;

    private FileAttachment(Path filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public static FileAttachment create(Path filePath, String fileName) {
        return new FileAttachment(filePath, fileName);
    }

    public static FileAttachment create(File file, String fileName) {
        return create(file.toPath(), fileName);
    }

    public static FileAttachment create(String filePath, String fileName) {
        return create(Path.of(filePath), fileName);
    }

    public static FileAttachment create(Path filePath) {
        return new FileAttachment(filePath, filePath.getFileName().toString());
    }

    public static FileAttachment create(File file) {
        return create(file.toPath());
    }

    public static FileAttachment create(String filePath) {
        return create(Path.of(filePath));
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

}
