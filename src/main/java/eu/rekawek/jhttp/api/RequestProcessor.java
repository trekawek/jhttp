package eu.rekawek.jhttp.api;

import java.io.UncheckedIOException;

import aQute.bnd.annotation.ConsumerType;

/**
 * Request processor produces a response for the HTTP request.
 * 
 * @author Tomasz Rękawek
 *
 */
@ConsumerType
public interface RequestProcessor {

    /**
     * This method may send HTTP response to the client (using the mutable {@code response} parameter) and
     * return {@code true}. It may also return {@code false}, passing the control to other registered
     * processors.
     * 
     * @param request incoming HTTP request
     * @param response mutable object that allows to send response to the client
     * @return {@code true} if the response has been created or {@code false} if given request type is not
     * supported by the processor
     * @throws UncheckedIOException in case of the processing error
     */
    boolean process(HttpRequest request, HttpResponse response);
}
