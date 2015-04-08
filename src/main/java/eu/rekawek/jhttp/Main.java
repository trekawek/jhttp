package eu.rekawek.jhttp;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        final File serverRoot;
        if (args.length > 0) {
            serverRoot = new File(args[0]);
        } else {
            serverRoot = new File(".");
        }

        final HttpServer server = new HttpServer(serverRoot);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (IOException e) {
                    LOG.error("Can't stop server", e);
                }
            }
        });
        server.start();
    }

}
