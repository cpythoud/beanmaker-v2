package org.beanmaker.v2.email;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileAttachment {

    private static final Tika TIKA = new Tika();

    private final Path filePath;
    private final String fileName;
    private final String mimeType;

    private FileAttachment(Path filePath, String fileName, String mimeType) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String detectMimeType(File file) {
        try {
            return TIKA.detect(file);
        } catch (IOException ioex) {
            throw new RuntimeException(ioex);
        }
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static class Builder {
        private Path filePath;
        private String fileName;
        private String mimeType;

        private Builder() { }

        public Builder filePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder file(File file) {
            this.filePath = file.toPath();
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = Path.of(filePath);
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public FileAttachment build() {
            if (filePath == null) {
                throw new IllegalStateException("File path cannot be null");
            }
            if (fileName == null) {
                fileName = filePath.getFileName().toString();
            }
            if (mimeType == null) {
                mimeType = detectMimeType(filePath.toFile());
            }

            return new FileAttachment(filePath, fileName, mimeType);
        }
    }

}
