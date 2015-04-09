package eu.rekawek.jhttp.server;

public class Header {

    private final String name;

    private final String value;

    public Header(final String headerLine) {
        final int separatorIndex = headerLine.indexOf(": ");
        if (separatorIndex > 0) {
            this.name = headerLine.substring(0, separatorIndex);
            this.value = headerLine.substring(separatorIndex);
        } else {
            this.name = headerLine;
            this.value = null;
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
