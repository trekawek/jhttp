package eu.rekawek.jhttp.processor;

import java.io.File;

import eu.rekawek.jhttp.FileResolver;
import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class ResourceNotFound implements RequestProcessor {

    private final FileResolver fileResolver;

    public ResourceNotFound(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public boolean process(HttpRequest request, HttpResponse response) {
        final File file = fileResolver.resolveFile(request);
        if (file.exists()) {
            return false;
        }
        response.setStatus(404, "File not found");
        response.setBody(String.format("File %s can't be found.", request.getRequestLine().getUri()));
        return true;
    }
}
