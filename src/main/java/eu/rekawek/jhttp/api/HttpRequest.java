package eu.rekawek.jhttp.api;

import java.io.File;

public interface HttpRequest {

    String getUri();

    String getHeaderValue(String name);

    File resolveFile();

    String getHttpVersion();

}
