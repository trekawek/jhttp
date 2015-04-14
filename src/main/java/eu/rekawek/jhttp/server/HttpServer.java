package eu.rekawek.jhttp.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;

import eu.rekawek.jhttp.api.RequestProcessor;
import eu.rekawek.jhttp.processor.DirectoryIndex;
import eu.rekawek.jhttp.processor.DirectoryListing;
import eu.rekawek.jhttp.processor.ResourceNotFound;
import eu.rekawek.jhttp.processor.StaticFile;

/**
 * This class creates a server socket, registers all processors and for each connection creates a
 * {@link ConnectionHandler} running in the new pooled thread.
 * 
 * @author Tomasz Rękawek
 *
 */
public class HttpServer {

    /**
     * Port to listen.
     */
    public static final int HTTP_PORT = 8888;

    /**
     * Size of the thread pool.
     */
    private static final int THREADS_NO = 10;

    private final ExecutorService executor;

    private final List<RequestProcessor> processors;

    private final PathResolver fileResolver;

    private volatile ServerSocket serverSocket;

    public HttpServer(final Path serverRoot) {
        this.executor = Executors.newFixedThreadPool(THREADS_NO);
        this.fileResolver = new PathResolver(serverRoot);

        processors = new ArrayList<>();
        processors.add(new DirectoryIndex());
        processors.add(new DirectoryListing());
        processors.add(new StaticFile());
        processors.add(new ResourceNotFound());
    }

    /**
     * Start listening.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(HTTP_PORT);
            do {
                final Socket clientSocket = serverSocket.accept();
                final ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket, processors,
                        fileResolver);
                executor.submit(connectionHandler);
            } while (!serverSocket.isClosed());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Stop listening.
     */
    public void stop() {
        IOUtils.closeQuietly(serverSocket);
    }

    /**
     * @return true if the server is bound
     */
    public boolean isBound() {
        return serverSocket != null;
    }
}
