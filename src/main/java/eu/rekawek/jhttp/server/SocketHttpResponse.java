package eu.rekawek.jhttp.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;

/**
 * This class wraps the socket's output stream and allows to send HTTP request status, headers and response
 * body.
 * 
 * @author Tomasz Rękawek
 */
public class SocketHttpResponse implements HttpResponse {

    private static final int HTTP_OK_STATUS = 200;

    private final String httpVersion;

    private final OutputStream outputStream;

    private final HeaderList headerList;

    private PrintWriter printWriter;

    private boolean outputStreamReturned;

    private boolean commited;

    private int statusCode = HTTP_OK_STATUS;

    private String statusMessage = "OK";

    public SocketHttpResponse(Socket clientSocket, HttpRequest request) throws IOException {
        this.outputStream = clientSocket.getOutputStream();
        this.httpVersion = request.getHttpVersion();
        this.headerList = new HeaderList();
    }

    @Override
    public void setStatus(int code, String message) {
        checkIfResponseNotCommitted();
        this.statusCode = code;
        this.statusMessage = message;
    }

    @Override
    public void addHeader(String name, String value) {
        checkIfResponseNotCommitted();
        headerList.addHeader(name, value);
    }

    @Override
    public void setHeader(String name, String value) {
        if (commited) {
            throw new IllegalStateException("Response has been committed");
        }
        headerList.setHeader(name, value);
    }

    @Override
    public void setContentType(String contentType) {
        checkIfResponseNotCommitted();
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
        outputStreamReturned = true;
        return outputStream;
    }

    /**
     * Sends the set headers and status code. After invoking this method it's impossible to modify the header
     * list or the response status.
     */
    public void commit() {
        if (commited) {
            return;
        }
        commited = true;
        final PrintWriter writer = new PrintWriter(outputStream);
        writer.println(String.format("%s %d %s", httpVersion, statusCode, statusMessage));
        headerList.getHeaders().stream().forEach(writer::println);
        writer.println();
        writer.flush();
    }

    /**
     * Call flush on the created {@link PrintWriter} or the {@link OutputStream}.
     */
    public void flush() throws IOException {
        if (printWriter != null) {
            printWriter.flush();
        } else {
            outputStream.flush();
        }
    }

    private void checkIfResponseNotCommitted() {
        if (commited) {
            throw new IllegalStateException("Response has been committed");
        }
    }
}
