package eu.rekawek.jhttp.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SocketHttpRequestTest {

    @Test
    public void testRequest() throws IOException {
        final SocketHttpRequest request = createRequest("/request/request.txt");
        assertEquals("xyz", request.getHeaderValue("content-type"));
        assertEquals("123", request.getHeaderValue("custom-header"));
        assertEquals("/my-site", request.getUri());
        assertEquals("HTTP/1.1", request.getHttpVersion());
    }

    private static SocketHttpRequest createRequest(String requestFilePath) throws IOException {
        final Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(
                SocketHttpRequestTest.class.getResourceAsStream(requestFilePath));
        return new SocketHttpRequest(socket, new PathResolver(Paths.get("/server/root")));
    }
}
