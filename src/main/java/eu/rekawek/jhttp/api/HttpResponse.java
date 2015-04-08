package eu.rekawek.jhttp.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class HttpResponse {

    private int statusCode = 200;

    private String statusMessage = "OK";

    private InputStream stream = new ByteArrayInputStream(new byte[0]);

    private List<Header> headers = new ArrayList<Header>();

    public List<Header> getHeaders() {
        return headers;
    }

    public InputStream getInputStream() {
        return stream;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatus(int code, String message) {
        this.statusCode = code;
        this.statusMessage = message;
    }

    public void setInputStream(InputStream stream) {
        this.stream = stream;
    }

    public void setBody(String body) {
        setInputStream(IOUtils.toInputStream(body));
    }

    public void addHeader(String name, String value) {
        headers.add(new Header(name, value));
    }

}
