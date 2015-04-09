package eu.rekawek.jhttp.server;

import java.io.IOException;
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

    private final Socket clientSocket;

    private final List<RequestProcessor> processors;

    private final FileResolver fileResolver;

    public ConnectionHandler(Socket clientSocket, List<RequestProcessor> processors, FileResolver fileResolver) {
        this.clientSocket = clientSocket;
        this.processors = processors;
        this.fileResolver = fileResolver;
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
        final HttpRequest request = new SocketHttpRequest(clientSocket, fileResolver);
        final SocketHttpResponse response = new SocketHttpResponse(clientSocket, request);

        try {
            process(request, response);
        } catch (IOException e) {
            response.setStatus(500, "Server error");
            response.setHeader("Connection", "Close");
            response.getPrintWriter().println(String.format("Server error: " + e.getMessage()));
        }

        response.commit();
        response.flush();
    }

    private void process(HttpRequest request, HttpResponse response) throws IOException {
        for (final RequestProcessor processor : processors) {
            if (processor.process(request, response)) {
                break;
            }
        }
    }

}
