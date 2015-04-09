package eu.rekawek.jhttp.processor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import eu.rekawek.jhttp.api.HttpRequest;
import eu.rekawek.jhttp.api.HttpResponse;
import eu.rekawek.jhttp.api.RequestProcessor;

/**
 * This processors renders directory listing.
 * 
 * @author Tomasz Rękawek
 *
 */
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
            appendLink("..", uri, writer);
        }
        Arrays.stream(file.listFiles())
            .map(File::getName)
            .forEach(s -> appendLink(s, uri, writer));
        return true;
    }
    
    private void appendLink(String fileName, String uri, PrintWriter writer) {
        writer.append("<li>\n<a href=\"").append(uri).append('/').append(fileName).append("\">");
        writer.append(fileName).append("</a>\n</li>\n");
    }
}
