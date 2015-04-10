package eu.rekawek.jhttp.server;

/**
 * A HTTP request or response header.
 * 
 * @author Tomasz Rękawek
 *
 */
public class Header {

    private final String name;

    private final String value;

    /**
     * Parse the header in form:
     * 
     * <pre>
     * Header-Name: header-value
     * </pre>
     * 
     * @param headerLine header in a form as above
     */
    public static Header parse(String headerLine) {
        final int separatorIndex = headerLine.indexOf(": ");
        if (separatorIndex > 0) {
            return new Header(headerLine.substring(0, separatorIndex), headerLine.substring(separatorIndex));
        } else {
            return new Header(headerLine, null);
        }
    }

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return name;
        } else {
            return String.format("%s:%s", name, value);
        }
    }
}
