package eu.rekawek.jhttp.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * A class representing header list, used by the request and response. It may contain many headers with the
 * same name.
 * 
 * @author Tomasz Rękawek
 *
 */
public class HeaderList {

    private final List<Header> headers = new ArrayList<Header>();

    /**
     * Add header
     * 
     * @param header to add
     */
    public void addHeader(Header header) {
        headers.add(header);
    }

    /**
     * Add header
     * 
     * @param name of the header to add
     * @param value of the header to add
     */
    public void addHeader(String name, String value) {
        headers.add(new Header(name, value));
    }

    /**
     * Adds the header or replaces if there is already a header with the given name.
     * If there is multiple headers with the given name, only the first one is replaced.
     * 
     * @param name of the header to set, case-insensitive
     * @param value of the header to set
     */
    public void setHeader(String name, String value) {
        final Header newHeader = new Header(name, value);
        boolean replaced = false;
        final ListIterator<Header> li = headers.listIterator();
        while (li.hasNext()) {
            if (name.equalsIgnoreCase(li.next().getName())) {
                replaced = true;
                li.set(newHeader);
                break;
            }
        }
        if (!replaced) {
            addHeader(name, value);
        }
    }
    
    /**
     * Return values of the all headers with the given name
     * 
     * @param name of the headers to return
     * @return list of the header values
     */
    public List<String> getHeaders(String name) {
        return headers
                .stream()
                .filter(h -> name.equalsIgnoreCase(h.getName()))
                .map(Header::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Return value of the header with the given name
     * 
     * @param name of the header to return
     * @return value of the header
     */
    public String getHeader(String name) {
        return getHeaders(name)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Return list of the all headers
     * 
     * @return All headers added to this {@link HeaderList}
     */
    public List<Header> getHeaders() {
        return Collections.unmodifiableList(headers);
    }
}
