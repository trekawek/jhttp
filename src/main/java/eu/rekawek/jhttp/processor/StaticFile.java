package eu.rekawek.jhttp.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class StaticFile implements RequestProcessor {

    @Override
    public boolean process(HttpRequest request, HttpResponse response) throws IOException {
        final File file = request.resolveFile();
        if (!file.isFile()) {
            return false;
        }

        serveFile(file, response);
        return true;
    }

    static void serveFile(File file, HttpResponse response) throws IOException {
        final String contentType = URLConnection.guessContentTypeFromName(file.getName());
        if (contentType != null) {
            response.addHeader("Content-Type", contentType);
        }
        try (final InputStream is = new FileInputStream(file)) {
            IOUtils.copy(is, response.getOutputStream());
        }
    }
}
