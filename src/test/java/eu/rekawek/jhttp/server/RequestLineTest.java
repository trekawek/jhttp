package eu.rekawek.jhttp.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestLineTest {

    @Test
    public void testParseRequestLine() {
        testParse("GET /xyz", "GET", "/xyz", "HTTP/1.0");
        testParse("GET /xyz HTTP/1.1", "GET", "/xyz", "HTTP/1.1");
        testParse("GET /xyz HTTP/1.1 asd", "GET", "/xyz", "HTTP/1.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyLine() {
        RequestLine.parse("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noUri() {
        RequestLine.parse("GET");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noMethod() {
        RequestLine.parse(" /xyz");
    }

    private static void testParse(String requestLine, String method, String uri, String version) {
        final RequestLine l = RequestLine.parse(requestLine);
        assertEquals(method, l.getMethod());
        assertEquals(uri, l.getUri());
        assertEquals(version, l.getVersion());
    }
}
