package eu.rekawek.jhttp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import eu.rekawek.jhttp.api.HttpRequest;

/**
 * This class parses the socket's input stream and exposes result in a form of a {@link HttpRequest}
 * implementation.
 * 
 * @author Tomasz Rękawek
 *
 */
public class SocketHttpRequest implements HttpRequest {

    private final RequestLine requestLine;

    private final List<Header> headers;

    private final PathResolver fileResolver;

    public SocketHttpRequest(Socket clientSocket, PathResolver fileResolver) throws IOException {
        this.fileResolver = fileResolver;

        final Reader reader = new InputStreamReader(clientSocket.getInputStream());
        final BufferedReader bufferedReader = new BufferedReader(reader);
        requestLine = RequestLine.parse(bufferedReader.readLine());
        headers = bufferedReader
            .lines()
            .filter(StringUtils::isNotBlank)
            .map(Header::parse)
            .collect(Collectors.toList());
    }

    @Override
    public String getUri() {
        return requestLine.getUri();
    }

    @Override
    public Path resolvePath() {
        return fileResolver.resolveFile(getUri());
    }

    @Override
    public String getHttpVersion() {
        return requestLine.getVersion();
    }

    @Override
    public String getHeaderValue(String name) {
        return headers
            .stream()
            .filter(h -> name.equalsIgnoreCase(h.getName()))
            .findFirst()
            .map(h -> h.getValue())
            .orElse(null);
    }
}
