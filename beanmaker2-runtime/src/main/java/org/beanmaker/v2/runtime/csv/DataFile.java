package org.beanmaker.v2.runtime.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.DuplicateHeaderMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataFile {

    // ! CSVFormat is not immutable. For that reason, the format is duplicated in all Builder functions
    // ! and not just referenced.
    // * Default separator is the comma (,)
    private static final CSVFormat DEFAULT_FORMAT =
            CSVFormat.Builder.create(CSVFormat.EXCEL)
                    .setAllowMissingColumnNames(false)
                    .setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW)
                    .setHeader()
                    .build();

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static class Builder {
        private final File file;
        private CSVFormat csvFormat;
        private Charset charset;

        private  Builder(File file) {
            this.file = file;
        }

        public static Builder create(File file) {
            if (!file.exists())
                throw new IllegalArgumentException("File does not exist: " + file.getAbsolutePath());
            if (!file.isFile())
                throw new IllegalArgumentException("Not a regular file: " + file.getAbsolutePath());

            return new Builder(file);
        }

        public static Builder create(Path path) {
            return create(path.toFile());
        }

        public static Builder create(String filename) {
            return create(Path.of(filename));
        }

        public Builder setFormat(CSVFormat csvFormat) {
            this.csvFormat = csvFormat;
            return this;
        }

        public Builder setDelimiter(String delimiter) {
            CSVFormat.Builder formatBuilder;
            if (csvFormat == null)
                formatBuilder = CSVFormat.Builder.create(DEFAULT_FORMAT);
            else
                formatBuilder = CSVFormat.Builder.create(csvFormat);

            csvFormat = formatBuilder.setDelimiter(delimiter).build();
            return this;
        }

        public Builder setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public DataFile build() {
            return new DataFile(
                    file,
                    csvFormat == null ? CSVFormat.Builder.create(DEFAULT_FORMAT).build() : csvFormat,
                    charset == null ? DEFAULT_CHARSET : charset
            );
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final File file;
    private final CSVFormat csvFormat;
    private final Charset charset;

    private DataFile(File file, CSVFormat csvFormat, Charset charset) {
        this.file = file;
        this.csvFormat = csvFormat;
        this.charset = charset;
    }

    public DataEntries parseFile() {
        long lineNumber = 0;
        List<String> headers;
        var dataEntries = new ArrayList<DataEntry>();

        try (var parser = CSVParser.parse(file, charset, csvFormat)) {
            headers = new ArrayList<>(parser.getHeaderNames());
            logger.info("CSV parsed headers: {}", headers);
            logger.trace("Creating data entries");
            for (var record: parser) {
                lineNumber = parser.getCurrentLineNumber();
                logger.trace("Processing line: {}", lineNumber);
                var data = new HashMap<String, String>();
                for (String header: headers)
                    data.put(header, record.get(header));
                dataEntries.add(new DataEntry(lineNumber, data));
            }
        } catch (Throwable throwable) {
            logger.error("Exception at line number: {}", lineNumber, throwable);
            throw new RuntimeException("Exception at line number: " + lineNumber, throwable);
        }

        return new DataEntries(lineNumber, headers, dataEntries);
    }

    public File getFile() {
        return file;
    }

    public CSVFormat getCsvFormat() {
        return CSVFormat.Builder.create(csvFormat).build();  // * because CSVFormat is mutable
    }

    public Charset getCharset() {
        return charset;
    }

}
