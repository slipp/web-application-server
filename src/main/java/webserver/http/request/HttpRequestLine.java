package webserver.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import util.HttpRequestUtils;
import webserver.http.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod method;
    private final URI uri;
    private final Map<String, String> queries = new HashMap<>();
    private static final Set<String> staticFileExtensions = Set.of("html", "js", "css", "ttf", "woff");

    private HttpRequestLine(HttpMethod method, URI uri) {
        this.method = method;
        this.uri = uri;
        this.queries.putAll(HttpRequestUtils.parseQueryString(uri.getQuery()));
    }

    public static HttpRequestLine from(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) {
            throw new IllegalArgumentException("Invalid Http Request");
        }
        String[] requestLine = line.split(" ");
        if (requestLine.length != 3) {
            throw new IllegalArgumentException("Invalid Request Line");
        }
        HttpMethod method = HttpMethod.valueOf(requestLine[0]);
        URI uri = URI.create(requestLine[1]);
        return new HttpRequestLine(method, uri);
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.uri.getPath();
    }

    public Optional<String> getQuery(String key) {
        return Optional.ofNullable(this.queries.get(key));
    }

    public boolean isStaticFileRequest() {
        String path = getPath();
        int index = path.lastIndexOf(".");
        if (index == -1) {
            return false;
        }
        String extension = path.substring(index + 1);
        return staticFileExtensions.contains(extension);
    }
}
