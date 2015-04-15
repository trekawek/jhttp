package eu.rekawek.jhttp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.rekawek.jhttp.api.HttpRequest;
import static eu.rekawek.jhttp.LambdaUtils.takeWhile;

/**
 * This class parses the socket's input stream and exposes result in a form of a {@link HttpRequest}
 * implementation.
 * 
 * @author Tomasz Rękawek
 *
 */
public class SocketHttpRequest implements HttpRequest {

    private final RequestLine requestLine;

    private final PathResolver fileResolver;

    private final HeaderList headerList;

    public SocketHttpRequest(Socket clientSocket, PathResolver fileResolver) throws IOException {
        this.fileResolver = fileResolver;
        this.headerList = new HeaderList();

        final Reader reader = new InputStreamReader(clientSocket.getInputStream());
        final BufferedReader bufferedReader = new BufferedReader(reader);
        requestLine = RequestLine.parse(bufferedReader.readLine());

        takeWhile(bufferedReader.lines(), StringUtils::isNotBlank)
          .map(Header::parse)
          .forEach(headerList::addHeader);
    }

    @Override
    public String getUri() {
        return requestLine.getUri();
    }

    @Override
    public Path resolvePath() {
        return fileResolver.resolveFile(getUri());
    }

    @Override
    public String getHttpVersion() {
        return requestLine.getVersion();
    }

    @Override
    public String getHeaderValue(String name) {
        return headerList.getHeader(name);
    }

    @Override
    public List<String> getHeaderValues(String name) {
        return headerList.getHeaders(name);
    }
}
