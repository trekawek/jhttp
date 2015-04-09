package eu.rekawek.jhttp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

    private final FileResolver fileResolver;

    public SocketHttpRequest(Socket clientSocket, FileResolver fileResolver) throws IOException {
        this.fileResolver = fileResolver;

        final Reader reader = new InputStreamReader(clientSocket.getInputStream());
        final BufferedReader bufferedReader = new BufferedReader(reader);
        requestLine = new RequestLine(bufferedReader.readLine());
        headers = new ArrayList<Header>();
        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null) {
            if (headerLine.isEmpty()) {
                break;
            }
            headers.add(new Header(headerLine));
        }
    }

    @Override
    public String getUri() {
        return requestLine.getUri();
    }

    @Override
    public File resolveFile() {
        return fileResolver.resolveFile(getUri());
    }

    @Override
    public String getHttpVersion() {
        return requestLine.getVersion();
    }

    @Override
    public String getHeaderValue(String name) {
        return headers.stream()
                .filter(h -> name.equalsIgnoreCase(h.getName()))
                .findFirst()
                .map(h -> h.getValue())
                .orElse(null);

    }
}
