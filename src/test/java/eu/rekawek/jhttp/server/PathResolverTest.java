package eu.rekawek.jhttp.server;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class PathResolverTest {

    @Test
    public void testPathResolver() {
        final PathResolver resolver = new PathResolver(Paths.get("/server/root"));
        assertEquals("/server/root", resolver.resolveFile("/").toString());
        assertEquals("/server/root", resolver.resolveFile("").toString());
        assertEquals("/server/root/path", resolver.resolveFile("/path").toString());
    }
}
