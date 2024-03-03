package webserver.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpHeaders;
import webserver.http.HttpMethod;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpRequestLine line;
    private final HttpHeaders headers;
    private final HttpCookies cookies;
    private final HttpRequestBody body;

    private HttpRequest(HttpRequestLine line, HttpHeaders headers, HttpRequestBody body) {
        this.line = line;
        this.headers = headers;
        this.cookies = new HttpCookies(headers);
        this.body = body;
    }

    public static HttpRequest from(InputStream in) throws IOException  {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        HttpRequestLine line = HttpRequestLine.from(br);
        HttpHeaders headers = HttpHeaders.from(br);
        HttpRequestBody body = HttpRequestBody.from(br, headers);
        return new HttpRequest(line, headers, body);
    }

    public HttpMethod getMethod() {
        return this.line.getMethod();
    }

    public String getPath() {
        return this.line.getPath();
    }

    public Optional<String> getQuery(String key) {
        return this.line.getQuery(key);
    }

    public HttpRequestBody getBody() {
        return this.body;
    }

    public boolean isStaticFileRequest() {
        return line.isStaticFileRequest();
    }

    public Optional<String> getHeader(String key) {
        return headers.getHeader(key);
    }

    public Optional<String> getCookie(String key) {
        return cookies.getCookie(key);
    }
}
