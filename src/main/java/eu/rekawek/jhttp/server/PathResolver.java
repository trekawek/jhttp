package eu.rekawek.jhttp.server;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

/**
 * This class looks for a file specified by the URI in the configured server root.
 * 
 * @author Tomasz Rękawek
 *
 */
public class PathResolver {

    private final Path serverRoot;

    public PathResolver(Path serverRoot) {
        this.serverRoot = serverRoot;
    }

    public Path resolveFile(String uri) {
        return serverRoot.resolve(StringUtils.removeStart(uri, "/"));
    }
}
