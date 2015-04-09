package eu.rekawek.jhttp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class ConnectionHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);

    private static final int KEEP_ALIVE_TIMEOUT = 5;

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
        String requestLine = null;

        final Reader reader = new InputStreamReader(clientSocket.getInputStream());
        final BufferedReader bufferedReader = new BufferedReader(reader);

        while ((requestLine = waitForRequestLine(bufferedReader)) != null) {
            final HttpRequest request = new SocketHttpRequest(clientSocket, requestLine, bufferedReader,
                    fileResolver);
            final SocketHttpResponse response = new SocketHttpResponse(clientSocket, request);

            final String connectionHeader = getConnectionHeader(request);
            if (connectionHeader != null) {
                response.setHeader("Connection", connectionHeader);
            }
            boolean keepAlive = "keep-alive".equalsIgnoreCase(connectionHeader);

            try {
                process(request, response);
            } catch (IOException e) {
                keepAlive = false;
                response.setStatus(500, "Server error");
                response.setHeader("Connection", "Close");
                response.getPrintWriter().println(String.format("Server error: " + e.getMessage()));
            }

            response.commit();
            response.flush();

            if (keepAlive) {
                clientSocket.getOutputStream().write("0\n".getBytes());
                clientSocket.getOutputStream().flush();
            } else {
                break;
            }
        }
    }

    private String getConnectionHeader(HttpRequest request) {
        final String httpVersion = request.getHttpVersion();
        final String connection = request.getHeaderValue("Connection");
        if (connection != null) {
            return connection;
        } else if ("HTTP/1.1".equals(httpVersion)) {
            return "keep-alive";
        } else {
            return null;
        }
    }

    /**
     * This method waits for the request line for {@link #KEEP_ALIVE_TIMEOUT} seconds. If there is no line
     * sent to the connection, it closes the socket and returns {@code null}.
     * 
     * @return First line of the HTTP request or null.
     */
    private String waitForRequestLine(final BufferedReader bufferedReader) throws IOException,
            InterruptedException {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                IOUtils.closeQuietly(clientSocket);
            }
        }, KEEP_ALIVE_TIMEOUT * 1000);

        final long waitUntil = System.currentTimeMillis() + KEEP_ALIVE_TIMEOUT * 1000;
        while (waitUntil > System.currentTimeMillis()) {
            try {
                final String line = bufferedReader.readLine();
                if (line != null) {
                    timer.cancel();
                    return line;
                }
            } catch (SocketException e) {
                return null;
            }
            Thread.sleep(100);
        }
        return null;
    }

    private void process(HttpRequest request, HttpResponse response) throws IOException {
        for (final RequestProcessor processor : processors) {
            if (processor.process(request, response)) {
                break;
            }
        }
    }

}
