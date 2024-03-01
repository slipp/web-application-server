package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;
import util.HttpRequestUtils.Pair;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private HttpMethod method;
    private URI uri;
    private Map<String, String> params;
    private final Map<String, String> headers = new HashMap<>();
    private Map<String, String> body;
    private static final Set<String> staticFileExtensions = Set.of("html", "js", "css");

    private HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            parseHttpRequest(br);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static HttpRequest from(InputStream in) throws IOException {
        return new HttpRequest(in);
    }

    private void parseRequestLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Invalid Http Request");
        }
        String[] requestLine = line.split(" ");
        if (requestLine.length != 3) {
            throw new IllegalArgumentException("Invalid Request Line");
        }
        method = HttpMethod.valueOf(requestLine[0]);
        uri = URI.create(requestLine[1]);
        params = HttpRequestUtils.parseQueryString(uri.getQuery());
    }

    private void parseHeaders(List<String> headers) {
        headers.forEach(header -> {
            Pair pair = HttpRequestUtils.parseHeader(header);
            this.headers.put(pair.getKey(), pair.getValue());
        });
    }

    private void parseBody(BufferedReader br) throws IOException {
        if (this.headers.containsKey("Content-Length")) {
            String body = IOUtils.readData(br, Integer.parseInt(this.headers.get("Content-Length")));
            this.body = HttpRequestUtils.parseQueryString(body);
        }
    }

    private void parseHttpRequest(BufferedReader br) throws IOException {
        parseRequestLine(br.readLine());
        List<String> headers = new ArrayList<>();
        for (String header = br.readLine(); !header.isEmpty(); header = br.readLine()) {
            headers.add(header);
        }
        parseHeaders(headers);
        parseBody(br);
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Map<String, String> getRequestParams() {
        return this.params;
    }

    public String getRequestPath() {
        return uri.getPath();
    }

    public Map<String, String> getRequestBody() {
        return this.body;
    }

    public boolean isStaticFileRequest() {
        if (method != HttpMethod.GET) return false;
        int idx = uri.getPath().lastIndexOf(".");
        if (idx >= 0) {
            String extension = uri.getPath().substring(idx + 1);
            return staticFileExtensions.contains(extension);
        }
        return false;
    }
}
