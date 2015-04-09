package eu.rekawek.jhttp.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;

public class SocketHttpResponse implements HttpResponse {

    private final String httpVersion;

    private final OutputStream outputStream;

    private PrintWriter printWriter;

    private boolean outputStreamReturned;

    private boolean commited;

    private final List<Header> headers = new ArrayList<Header>();

    private int statusCode = 200;

    private String statusMessage = "OK";

    public SocketHttpResponse(Socket clientSocket, HttpRequest request) throws IOException {
        this.outputStream = clientSocket.getOutputStream();
        this.httpVersion = request.getHttpVersion();
    }

    @Override
    public void setStatus(int code, String message) {
        if (commited) {
            throw new IllegalStateException("Response has been committed");
        }
        this.statusCode = code;
        this.statusMessage = message;
    }

    @Override
    public void addHeader(String name, String value) {
        if (commited) {
            throw new IllegalStateException("Response has been committed");
        }
        headers.add(new Header(name, value));
    }

    @Override
    public void setHeader(String name, String value) {
        if (commited) {
            throw new IllegalStateException("Response has been committed");
        }
        for (int i = 0, s = headers.size(); i < s; i++) {
            if (name.equals(headers.get(i).getName())) {
                headers.set(i, new Header(name, value));
                return;
            }
        }
        addHeader(name, value);
    }

    @Override
    public void setContentType(String contentType) {
        if (commited) {
            throw new IllegalStateException("Response has been committed");
        }
        setHeader("Content-Type", contentType);
    }

    @Override
    public PrintWriter getPrintWriter() {
        if (!commited) {
            commit();
        }
        if (outputStreamReturned) {
            throw new IllegalStateException("getOutputStream() has been already called");
        }
        if (printWriter == null) {
            printWriter = new PrintWriter(outputStream);
        }
        return printWriter;
    }

    @Override
    public OutputStream getOutputStream() {
        if (!commited) {
            commit();
        }
        if (printWriter != null) {
            throw new IllegalStateException("getPrintWriter() has been already called");
        }
        return outputStream;
    }

    public void commit() {
        if (commited) {
            return;
        }
        commited = true;
        final PrintWriter writer = new PrintWriter(outputStream);
        writer.println(String.format("%s %d %s", httpVersion, statusCode, statusMessage));
        for (final Header header : headers) {
            writer.println(header);
        }
        writer.println();
        writer.flush();
    }

    public void flush() throws IOException {
        if (printWriter != null) {
            printWriter.flush();
        } else {
            outputStream.flush();
        }
    }
}
