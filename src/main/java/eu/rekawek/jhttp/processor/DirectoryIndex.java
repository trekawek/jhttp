package eu.rekawek.jhttp.processor;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

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
    public boolean process(HttpRequest request, HttpResponse response) throws UncheckedIOException {
        final Path directory = request.resolvePath();
        if (!Files.isDirectory(directory)) {
            return false;
        }

        return Arrays.stream(INDEX_FILE_NAMES)
            .map(directory::resolve)
            .filter(Files::exists)
            .findFirst()
            .map(curry(StaticFile::serveFile, response))
            .orElse(false);
    }
    
    private static <T, U, R> Function<U, R> curry(BiFunction<T, U, R> function, T t) {
        return u -> function.apply(t, u);
    }
}
