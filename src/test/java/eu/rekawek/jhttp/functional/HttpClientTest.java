package eu.rekawek.jhttp.functional;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.rekawek.jhttp.server.HttpServer;
import static org.junit.Assert.*;

public class HttpClientTest {

    private static final int TEST_HTTP_PORT = 34567;

    private final HttpClient client = HttpClientBuilder.create().build();

    private final HttpServer server = new HttpServer(Paths.get("src/test/resources/http-server/server-root"), TEST_HTTP_PORT, 1);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Before
    public void setUp() throws IOException, InterruptedException {
        executor.submit(() -> server.start());
        waitForTheServer();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testDirectoryListing() throws IOException {
        testRequest("/", "/http-server/root.html");
    }

    @Test
    public void testDirectoryIndex() throws IOException {
        testRequest("/directory1", "/http-server/server-root/directory1/index.html");
    }

    @Test
    public void testStaticFile() throws IOException {
        testRequest("/file1.html", "/http-server/server-root/file1.html");
        testRequest("/file2.html", "/http-server/server-root/file2.html");
    }

    @Test
    public void test404() throws IOException {
        testRequest("/invalid-file.html", "/http-server/404.html", 404);
    }

    private void testRequest(String uri, String responsePath) throws ParseException, IOException {
        testRequest(uri, responsePath, 200);
    }

    private void testRequest(String uri, String responsePath, int expectedStatus) throws ParseException,
            IOException {
        final HttpResponse response = getResponse(uri);
        assertEquals(expectedStatus, response.getStatusLine().getStatusCode());
        final String result = EntityUtils.toString(response.getEntity());
        final String expected = IOUtils.toString(this.getClass().getResourceAsStream(responsePath));
        assertEquals(expected, result);
    }

    private HttpResponse getResponse(String uri) throws IOException {
        final HttpUriRequest request = new HttpGet("http://localhost:" + TEST_HTTP_PORT + uri);
        return client.execute(request);
    }

    private static void waitForTheServer() throws IOException, UnknownHostException, InterruptedException {
        boolean opened = false;
        for (int i = 0; i < 10; i++) {
            try {
                new Socket("localhost", TEST_HTTP_PORT).close();
                opened = true;
                break;
            } catch (ConnectException e) {
                Thread.sleep(100);
            }
        }
        assertTrue(opened);
    }

}
