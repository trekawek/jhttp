package eu.rekawek.jhttp.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLConnection;

import eu.rekawek.jhttp.FileResolver;
import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class StaticFile implements RequestProcessor {

    private final FileResolver fileResolver;

    public StaticFile(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public boolean process(HttpRequest request, HttpResponse response) throws FileNotFoundException {
        final File file = fileResolver.resolveFile(request);
        if (!file.isFile()) {
            return false;
        }

        serveFile(file, response);
        return true;
    }

    static void serveFile(File file, HttpResponse response) throws FileNotFoundException {
        final String contentType = URLConnection.guessContentTypeFromName(file.getName());
        if (contentType != null) {
            response.addHeader("Content-Type", contentType);
        }
        response.setInputStream(new FileInputStream(file));
    }
}
