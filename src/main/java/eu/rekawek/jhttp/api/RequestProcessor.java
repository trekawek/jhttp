package eu.rekawek.jhttp.api;

import java.io.IOException;

public interface RequestProcessor {
    boolean process(HttpRequest request, HttpResponse response) throws IOException;
}
