package eu.rekawek.jhttp.api;

import java.nio.file.Path;
import java.util.List;

import aQute.bnd.annotation.ProviderType;

/**
 * An incoming request.
 * 
 * @author Tomasz Rękawek
 *
 */
@ProviderType
public interface HttpRequest {

    /**
     * Returns HTTP URI, representing the requested file.
     * 
     * @return HTTP URI
     */
    String getUri();

    /**
     * Get a header value by name
     * 
     * @param name Header name, case insensitive.
     * @return Header value or {@code null} if there is no such header.
     */
    String getHeaderValue(String name);

    /**
     * Get a header values by name
     * 
     * @param name Header name, case insensitive.
     * @return Header values or an empty list if there is no such header.
     */
    List<String> getHeaderValues(String name);

    /**
     * Uses request URI to find a requested file inside the server root directory.
     * 
     * @return Resolved file.
     */
    Path resolvePath();

    /**
     * Version of the HTTP protocol, usually {@code HTTP/1.1}
     * 
     * @return HTTP version.
     */
    String getHttpVersion();

}
