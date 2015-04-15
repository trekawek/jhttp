package eu.rekawek.jhttp.processor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

/**
 * This processor renders a static file.
 * 
 * @author Tomasz Rękawek
 *
 */
public class StaticFile implements RequestProcessor {

    @Override
    public boolean process(HttpRequest request, HttpResponse response) {
        final Path file = request.resolvePath();
        if (!Files.isReadable(file)) {
            return false;
        }

        serveFile(response, file);
        return true;
    }

    static boolean serveFile(HttpResponse response, Path file) {
        final String contentType = URLConnection.guessContentTypeFromName(file.toString());
        if (contentType != null) {
            response.setContentType(contentType);
        }
        try {
            Files.copy(file, response.getOutputStream());
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
