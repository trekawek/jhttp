package eu.rekawek.jhttp.processor;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import eu.rekawek.jhttp.FileResolver;
import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class DirectoryListing implements RequestProcessor {

    private final FileResolver fileResolver;

    public DirectoryListing(FileResolver fileResolver) {
        this.fileResolver = fileResolver;
    }

    public boolean process(HttpRequest request, HttpResponse response) {
        final File file = fileResolver.resolveFile(request);
        if (!file.isDirectory()) {
            return false;
        }

        final String uri = StringUtils.removeEnd(request.getRequestLine().getUri(), "/");
        final StringBuilder builder = new StringBuilder();
        if (!uri.isEmpty()) {
            builder.append("<li><a href=\"..\">..</a></li>");
        }
        for (File f : file.listFiles()) {
            final String name = f.getName();
            builder.append("<li><a href=\"").append(uri).append('/').append(name).append("\">");
            builder.append(f.getName()).append("</a></li>");
        }
        response.addHeader("Content-Type", "text/html");
        response.setInputStream(IOUtils.toInputStream(builder.toString()));
        return true;
    }
}
