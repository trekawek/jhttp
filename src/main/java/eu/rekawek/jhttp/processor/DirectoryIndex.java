package eu.rekawek.jhttp.processor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

/**
 * This processor renders the {@code index.htm[l]} file, if it exists in the requested directory.
 * 
 * @author Tomasz Rękawek
 *
 */
public class DirectoryIndex implements RequestProcessor {

    private static final String[] INDEX_FILE_NAMES = new String[] { "index.html", "index.htm" };

    @Override
    public boolean process(HttpRequest request, HttpResponse response) throws IOException {
        final File file = request.resolveFile();
        if (!file.isDirectory()) {
            return false;
        }

        final Optional<File> index =
                Arrays.stream(INDEX_FILE_NAMES)
                .map(s -> new File(file, s))
                .filter(f -> f.isFile())
                .findFirst();
        if (index.isPresent()) {
            StaticFile.serveFile(index.get(), response);
        }
        return index.isPresent();
    }
}
