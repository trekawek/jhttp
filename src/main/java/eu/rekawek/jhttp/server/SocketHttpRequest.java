package eu.rekawek.jhttp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import eu.rekawek.jhttp.api.HttpRequest;

public class SocketHttpRequest implements HttpRequest {

    private final String clientHostname;

    private final RequestLine requestLine;

    private final List<Header> headers;

    private final FileResolver fileResolver;

    public SocketHttpRequest(Socket clientSocket, String requestLine, BufferedReader reader,
            FileResolver fileResolver) throws IOException {
        this.fileResolver = fileResolver;
        this.requestLine = new RequestLine(requestLine);

        headers = new ArrayList<Header>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null) {
            if (headerLine.isEmpty()) {
                break;
            }
            headers.add(new Header(headerLine));
        }
        clientHostname = clientSocket.getInetAddress().getCanonicalHostName();
    }

    public String getClientHostname() {
        return clientHostname;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public List<Header> getHeaders() {
        return headers;
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
        for (Header h : headers) {
            if (name.equalsIgnoreCase(h.getName())) {
                return h.getValue();
            }
        }
        return null;
    }
}
