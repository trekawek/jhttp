package eu.rekawek.jhttp.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeaderTest {

    @Test
    public void testParseHeader() {
        testParse("Header: v123", "Header", "v123");
        testParse("Header: v:1:2:3", "Header", "v:1:2:3");
        testParse("Header: ", "Header", "");
        testParse("Header:", "Header:", "");
        testParse("Header", "Header", "");
    }

    @Test
    public void testFormatHeader() {
        assertEquals("Header: v123", new Header("Header", "v123").toString());
        assertEquals("Header: ", new Header("Header", "").toString());
        assertEquals("Header: ", new Header("Header", null).toString());
    }

    private static void testParse(String header, String name, String value) {
        final Header h = Header.parse(header);
        assertEquals(name, h.getName());
        assertEquals(value, h.getValue());
    }
}
