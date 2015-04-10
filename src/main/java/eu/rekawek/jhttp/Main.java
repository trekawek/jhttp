package eu.rekawek.jhttp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import eu.rekawek.jhttp.server.HttpServer;

public class Main {

    /**
     * Entry point for the server application. You may pass the server root directory path as the first
     * argument.
     */
    public static void main(String[] args) throws IOException {
        final Path serverRoot = Paths.get(args.length > 0 ? args[0] : ".");
        final HttpServer server = new HttpServer(serverRoot);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
        server.start();
    }
}
