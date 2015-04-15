package eu.rekawek.jhttp.processor;

import java.nio.file.Files;
import java.nio.file.Path;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

/**
 * This processors renders a 404 error if requested file doesn't exist in the server root.
 * 
 * @author Tomasz Rękawek
 *
 */
public class ResourceNotFound implements RequestProcessor {

    private static final int HTTP_PAGE_NOT_FOUND_STATUS = 404;

    public boolean process(HttpRequest request, HttpResponse response) {
        final Path path = request.resolvePath();
        if (Files.exists(path)) {
            return false;
        }
        response.setStatus(HTTP_PAGE_NOT_FOUND_STATUS, "File not found");
        response.getPrintWriter().println(String.format("File %s can't be found.", request.getUri()));
        return true;
    }
}
