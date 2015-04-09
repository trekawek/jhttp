package eu.rekawek.jhttp.processor;

import java.io.File;
import java.io.IOException;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class DirectoryIndex implements RequestProcessor {

    private static final String[] INDEX_FILE_NAMES = new String[] { "index.html", "index.htm" };

    @Override
    public boolean process(HttpRequest request, HttpResponse response) throws IOException {
        final File file = request.resolveFile();
        if (!file.isDirectory()) {
            return false;
        }

        for (final String indexName : INDEX_FILE_NAMES) {
            final File index = new File(file, indexName);
            if (index.isFile()) {
                StaticFile.serveFile(file, response);
                return true;
            }
        }
        return false;
    }

}
