package eu.rekawek.jhttp;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import eu.rekawek.jhttp.api.HttpRequest;

public class FileResolver {

    private final File serverRoot;

    public FileResolver(File serverRoot) {
        this.serverRoot = serverRoot;
    }

    public File resolveFile(HttpRequest request) {
        final String uri = request.getRequestLine().getUri();
        return new File(serverRoot, StringUtils.removeStart(uri, "/"));
    }
}
