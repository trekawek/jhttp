package eu.rekawek.jhttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.rekawek.jhttp.api.Header;
import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestLine;
import eu.rekawek.jhttp.api.RequestProcessor;

public class ConnectionHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);

    private final Socket clientSocket;

    private final List<RequestProcessor> processors;

    public ConnectionHandler(Socket clientSocket, List<RequestProcessor> processors) {
        this.clientSocket = clientSocket;
        this.processors = processors;
    }

    public void run() {
        try {
            handleConnection();
        } catch (IOException e) {
            LOG.error("Can't handle connection", e);
        }
    }

    private void handleConnection() throws IOException {
        final Reader reader = new InputStreamReader(clientSocket.getInputStream());
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final RequestLine requestLine = new RequestLine(bufferedReader.readLine());
        final List<Header> headers = new ArrayList<Header>();

        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null) {
            if (headerLine.isEmpty()) {
                break;
            }
            headers.add(new Header(headerLine));
        }
        final String clientHostName = clientSocket.getInetAddress().getCanonicalHostName();
        final HttpRequest request = new HttpRequest(clientHostName, requestLine, headers);
        final HttpResponse response = handleRequest(request);

        final OutputStream outputStream = clientSocket.getOutputStream();
        final Writer writer = new OutputStreamWriter(clientSocket.getOutputStream());
        final PrintWriter printWriter = new PrintWriter(writer);
        printWriter.println(getStatusLine(request, response));
        for (final Header header : response.getHeaders()) {
            printWriter.println(header);
        }
        printWriter.println();
        printWriter.flush();

        IOUtils.copy(response.getInputStream(), outputStream);
        clientSocket.close();
    }

    private HttpResponse handleRequest(HttpRequest request) {
        final HttpResponse response = new HttpResponse();
        try {
            for (final RequestProcessor processor : processors) {
                if (processor.process(request, response)) {
                    break;
                }
            }
        } catch (IOException e) {
            response.setStatus(500, "Server error");
            response.setBody(String.format("Server error: " + e.getMessage()));
        }
        return response;
    }

    private static String getStatusLine(HttpRequest request, HttpResponse response) {
        final String version = request.getRequestLine().getVersion();
        final int statusCode = response.getStatusCode();
        final String statusMessage = response.getStatusMessage();

        return String.format("%s %d %s", version, statusCode, statusMessage);
    }
}
