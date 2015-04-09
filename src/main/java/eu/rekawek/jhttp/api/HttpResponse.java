package eu.rekawek.jhttp.api;

import java.io.OutputStream;
import java.io.PrintWriter;

public interface HttpResponse {

    void addHeader(String name, String value);

    void setHeader(String name, String value);

    void setContentType(String contentType);

    void setStatus(int code, String message);

    PrintWriter getPrintWriter();

    OutputStream getOutputStream();

}
