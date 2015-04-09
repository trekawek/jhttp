package eu.rekawek.jhttp.api;

import java.io.OutputStream;
import java.io.PrintWriter;

import aQute.bnd.annotation.ProviderType;

/**
 * Response to the HTTP request.
 * 
 * @author Tomasz Rękawek
 *
 */
@ProviderType
public interface HttpResponse {

    /**
     * Adds new HTTP header.
     * 
     * @param name Header name
     * @param value Header value
     */
    void addHeader(String name, String value);

    /**
     * Sets the header with a given name. If a header with this name is already added, it'll be replaced.
     * 
     * @param name Header name
     * @param value Header value
     */
    void setHeader(String name, String value);

    /**
     * Sets the {@code Content-Type} header.
     * 
     * @param contentType Content type of the response
     */
    void setContentType(String contentType);

    /**
     * Sets the HTTP status code and the status message.
     * 
     * @param code Numeric HTTP status code
     * @param message Status message
     */
    void setStatus(int code, String message);

    /**
     * Creates and returns a {@link PrintWriter} object that allows to generate a textual response. After
     * calling this method you can't invoke {@link #getOutputStream()}.
     * 
     * @throws IllegalStateException if {@link #getOutputStream()} has been already called.
     * @return PrintWriter wrapping the HTTP response stream.
     */
    PrintWriter getPrintWriter();

    /**
     * Returns a {@link OutputStream} of the HTTP response. After calling this method you can't invoke
     * {@link #getPrintWriter()}.
     *
     * @throws IllegalStateException if {@link #getPrintWriter()} has been already called.
     * @return OutputStream of the response.
     */
    OutputStream getOutputStream();

}
