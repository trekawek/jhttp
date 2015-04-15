package eu.rekawek.jhttp.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

/**
 * The purpose of this class is to take the connected client socket, create a {@link HttpRequest} and
 * {@link HttpResponse} wrapping the socket input and output streams and find a {@link RequestProcessor} that
 * accepts the request.
 * 
 * @author Tomasz Rękawek
 *
 */
public class ConnectionHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);

    private static final int HTTP_SERVER_ERROR_STATUS = 500;

    private final Socket clientSocket;

    private final List<RequestProcessor> processors;

    private final PathResolver pathResolver;

    public ConnectionHandler(Socket clientSocket, List<RequestProcessor> processors, PathResolver pathResolver) {
        this.clientSocket = clientSocket;
        this.processors = processors;
        this.pathResolver = pathResolver;
    }

    public void run() {
        try {
            handleConnection();
        } catch (IOException | InterruptedException e) {
            LOG.error("Can't handle connection", e);
        } finally {
            IOUtils.closeQuietly(clientSocket);
        }
    }

    private void handleConnection() throws IOException, InterruptedException {
        final SocketHttpRequest request = new SocketHttpRequest(clientSocket, pathResolver);
        final SocketHttpResponse response = new SocketHttpResponse(clientSocket, request);

        try {
            process(request, response);
        } catch (UncheckedIOException e) {
            response.setStatus(HTTP_SERVER_ERROR_STATUS, "Server error");
            response.setHeader("Connection", "Close");
            response.getPrintWriter().println(String.format("Server error: " + e.getMessage()));
            LOG.error("Error in the request processing", e);
        }

        response.commit();
        response.flush();
    }

    private void process(HttpRequest request, HttpResponse response) {
        processors
            .stream()
            .filter(p -> p.process(request, response))
            .findFirst();
    }
}
