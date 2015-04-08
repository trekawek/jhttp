package eu.rekawek.jhttp.api;

import java.util.List;

public class HttpRequest {
    private final String clientHostname;

    private final RequestLine requestLine;

    private final List<Header> headers;

    public HttpRequest(String clientHostname, RequestLine requestLine, List<Header> headers) {
        this.clientHostname = clientHostname;
        this.requestLine = requestLine;
        this.headers = headers;
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

}
