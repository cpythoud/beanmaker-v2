package org.beanmaker.v2.email;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class EmbeddedImageFile {

    private final Path filePath;
    private final String name;

    private EmbeddedImageFile(Path filePath, String name) {
        this.filePath = filePath;
        this.name = name;
    }

    public static EmbeddedImageFile create(Path filePath, String name) {
        return new EmbeddedImageFile(filePath, name);
    }

    public static EmbeddedImageFile create(File file, String name) {
        return create(file.toPath(), name);
    }

    public static EmbeddedImageFile create(String filePath, String name) {
        return create(Path.of(filePath), name);
    }

    public static EmbeddedImageFile create(Path filePath) {
        return new EmbeddedImageFile(filePath, filePath.getFileName().toString());
    }

    public static EmbeddedImageFile create(File file) {
        return create(file.toPath());
    }

    public static EmbeddedImageFile create(String filePath) {
        return create(Path.of(filePath));
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getName() {
        return name;
    }

    public String getBase64Content() {
        try {
            byte[] fileContent = Files.readAllBytes(filePath);
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
