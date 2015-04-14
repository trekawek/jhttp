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

public class ResourceNotFoundTest {

    private Path tempDir;

    private StringWriter stringWriter;

    private HttpRequest request;

    private HttpResponse response;

    @Before
    public void setup() throws IOException {
        stringWriter = new StringWriter();
        tempDir = Files.createTempDirectory("jhttp-test");

        request = mock(HttpRequest.class);
        when(request.resolvePath()).thenReturn(tempDir.resolve("asdxyz"));
        when(request.getUri()).thenReturn("/asdxyz");

        response = mock(HttpResponse.class);
        when(response.getPrintWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @After
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(tempDir.toFile());
    }

    @Test
    public void testRootListing() throws IOException {
        assertTrue(new ResourceNotFound().process(request, response));
        verify(response).setStatus(404, "File not found");
        assertEquals(readResource("/not-found/not-found.html"), stringWriter.toString());
    }

    private String readResource(String filename) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(filename));
    }
}
