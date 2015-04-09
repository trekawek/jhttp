package eu.rekawek.jhttp.server;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class FileResolver {

    private final File serverRoot;

    public FileResolver(File serverRoot) {
        this.serverRoot = serverRoot;
    }

    public File resolveFile(String uri) {
        return new File(serverRoot, StringUtils.removeStart(uri, "/"));
    }
}
