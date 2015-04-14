package eu.rekawek.jhttp.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DirectoryListingTest {

    private Path tempDir;

    private StringWriter stringWriter;

    private HttpRequest request;

    private HttpResponse response;

    @Before
    public void setup() throws IOException {
        stringWriter = new StringWriter();
        tempDir = Files.createTempDirectory("jhttp-test");

        request = mock(HttpRequest.class);
        when(request.resolvePath()).thenReturn(tempDir);
        when(request.getUri()).thenReturn("/");

        response = mock(HttpResponse.class);
        when(response.getPrintWriter()).thenReturn(new PrintWriter(stringWriter));

        Files.write(tempDir.resolve("file1"), "test123".getBytes());
        Files.write(tempDir.resolve("file2"), "test123".getBytes());
        Files.write(tempDir.resolve("file3"), "test123".getBytes());
        Files.createDirectories(tempDir.resolve("some-dir"));
        Files.write(tempDir.resolve("some-dir/xyz"), "test123".getBytes());
    }

    @After
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(tempDir.toFile());
    }

    @Test
    public void testRootListing() throws IOException {
        assertTrue(new DirectoryListing().process(request, response));
        assertEquals(readResource("/directory-listing/root.html"), stringWriter.toString());
    }

    @Test
    public void testDirListing() throws IOException {
        when(request.resolvePath()).thenReturn(tempDir.resolve("some-dir"));
        when(request.getUri()).thenReturn("/some-dir");
        assertTrue(new DirectoryListing().process(request, response));
        assertEquals(readResource("/directory-listing/some-dir.html"), stringWriter.toString());
    }

    private String readResource(String filename) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(filename));
    }
}
