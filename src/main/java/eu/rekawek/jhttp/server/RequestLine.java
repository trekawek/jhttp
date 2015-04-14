package eu.rekawek.jhttp.server;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents the first line of the HTTP request, eg.:
 * 
 * <pre>
 * GET /index.html HTTP/1.1
 * </pre>
 * 
 * @author Tomasz Rękawek
 *
 */
public class RequestLine {

    private final String method;

    private final String uri;

    private final String version;

    private RequestLine(String method, String uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    /**
     * Parse the request line string.
     */
    public static RequestLine parse(final String requestLine) {
        final String[] split = StringUtils.split(requestLine, ' ');
        final String method, uri, version;
        if (split.length >= 2) {
            method = split[0];
            uri = split[1];
        } else {
            throw new IllegalArgumentException(requestLine + " is not a valid HTTP request line");
        }
        if (split.length >= 3) {
            version = split[2];
        } else {
            version = "HTTP/1.0";
        }
        return new RequestLine(method, uri, version);
    }

    /**
     * @return requested URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return HTTP protocol version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return HTTP request method (GET, POST, etc.)
     */
    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        if (version != null) {
            return String.format("%s %s %s", method, uri, version);
        } else {
            return String.format("%s %s", method, uri);
        }
    }

}
