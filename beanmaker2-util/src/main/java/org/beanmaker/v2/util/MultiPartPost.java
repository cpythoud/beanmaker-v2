package org.beanmaker.v2.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;

public final class MultiPartPost implements Closeable {

    private static final String CRLF = "\r\n"; // * line separator required by multipart/form-data
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final URLConnection connection;
    private final String boundary;
    private final Charset charset;
    private final OutputStream output;
    private final OutputStreamWriter outputStreamWriter;
    private final PrintWriter writer;

    public MultiPartPost(String url) throws IOException {
        this(url, DEFAULT_CHARSET, getDefaultBoundary());
    }

    public static String getDefaultBoundary() {
        return Long.toHexString(System.currentTimeMillis());
    }

    public MultiPartPost(String url, Charset charset) throws IOException {
        this(url, charset, getDefaultBoundary());
    }

    public MultiPartPost(String url, String boundary) throws IOException {
        this(url, DEFAULT_CHARSET, boundary);
    }

    public MultiPartPost(String url, Charset charset, String boundary) throws IOException {
        this(url, charset, boundary, null);
    }

    public MultiPartPost(String url, Charset charset, String boundary, String cookie) throws IOException {
        connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        if (cookie != null)
            connection.setRequestProperty("Cookie", cookie);

        this.charset = charset;
        this.boundary = boundary;

        output = connection.getOutputStream();
        outputStreamWriter = new OutputStreamWriter(output, charset);
        writer = new PrintWriter(outputStreamWriter, true);
    }

    public void addFormParameter(String name, String value) {
        writer.append("--").append(boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(CRLF);
        writer.append("Content-Type: text/plain; charset=").append(charset.name()).append(CRLF);
        writer.append(CRLF).append(value).append(CRLF);
    }

    public void addFormParameter(String name, Object value) {
        addFormParameter(name, value.toString());
    }

    public void addFormParameter(String name, long value) {
        addFormParameter(name, Long.toString(value));
    }

    public void addTextFile(String parameterName, File textFile) throws IOException {
        writer.append("--").append(boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"").append(parameterName)
                .append("\"; filename=\"").append(textFile.getName()).append("\"").append(CRLF);
        writer.append("Content-Type: text/plain; charset=").append(charset.name()).append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(textFile.toPath(), output);
        output.flush(); // ! Important before continuing with writer !
        writer.append(CRLF).flush();
    }

    public void addBinaryFile(String parameterName, File binaryFile) throws IOException {
        writer.append("--").append(boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"").append(parameterName)
                .append("\"; filename=\"").append(binaryFile.getName()).append("\"").append(CRLF);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
        writer.append(CRLF).flush();
        Files.copy(binaryFile.toPath(), output);
        output.flush(); // ! Important before continuing with writer !
        writer.append(CRLF).flush();
    }

    public int sendRequest() throws IOException {
        // * End multipart/form-data *
        writer.append("--").append(boundary).append("--").append(CRLF).flush();

        return ((HttpURLConnection) connection).getResponseCode();
    }

    @Override
    public void close() throws IOException {
        writer.close();
        outputStreamWriter.close();
        output.close();
    }

}
