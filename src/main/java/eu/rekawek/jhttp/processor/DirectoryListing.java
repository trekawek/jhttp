package eu.rekawek.jhttp.processor;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

public class DirectoryListing implements RequestProcessor {

    public boolean process(HttpRequest request, HttpResponse response) {
        final File file = request.resolveFile();
        if (!file.isDirectory()) {
            return false;
        }
        response.addHeader("Content-Type", "text/html");

        final String uri = StringUtils.removeEnd(request.getUri(), "/");
        final PrintWriter writer = response.getPrintWriter();
        if (!uri.isEmpty()) {
            writer.println("<li><a href=\"..\">..</a></li>");
        }
        for (File f : file.listFiles()) {
            final String name = f.getName();
            writer.append("<li>\n<a href=\"").append(uri).append('/').append(name).append("\">");
            writer.append(f.getName()).append("</a>\n</li>\n");
        }
        return true;
    }
}
