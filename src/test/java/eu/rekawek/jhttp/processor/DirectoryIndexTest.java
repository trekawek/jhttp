package eu.rekawek.jhttp.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DirectoryIndexTest {

    private Path tempDir;

    private ByteArrayOutputStream outputStream;

    private HttpRequest request;

    private HttpResponse response;

    @Before
    public void setup() throws IOException {
        outputStream = new ByteArrayOutputStream();
        tempDir = Files.createTempDirectory("jhttp-test");

        request = mock(HttpRequest.class);
        when(request.resolvePath()).thenReturn(tempDir);

        response = mock(HttpResponse.class);
        when(response.getOutputStream()).thenReturn(outputStream);
    }

    @After
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(tempDir.toFile());
    }

    @Test
    public void testIndexHtml() throws IOException {
        Files.write(tempDir.resolve("index.html"), "test123".getBytes());
        assertTrue(new DirectoryIndex().process(request, response));
        assertEquals("test123", getResponseAsString());
    }

    @Test
    public void testIndexHtm() throws IOException {
        Files.write(tempDir.resolve("index.htm"), "test123".getBytes());
        assertTrue(new DirectoryIndex().process(request, response));
        assertEquals("test123", getResponseAsString());
    }

    @Test
    public void testNoIndex() throws IOException {
        Files.write(tempDir.resolve("someFile"), "test123".getBytes());
        assertFalse(new DirectoryIndex().process(request, response));
    }

    private String getResponseAsString() {
        return new String(outputStream.toByteArray());
    }
}
