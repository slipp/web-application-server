package webserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final HttpStatus status;
    private final Map<String, String> headers = new HashMap<>();
    private final byte[] body;

    private HttpResponse(HttpStatus status, byte[] body) {
        this.status = status;
        this.body = body;
        if (body.length > 0) {
            addHeader("Content-Length", String.valueOf(body.length));
        }
    }

    public static HttpResponse of(HttpStatus status, String body) {
        return new HttpResponse(status, body.getBytes());
    }

    public static HttpResponse of(HttpStatus status, byte[] body) {
        return new HttpResponse(status, body);
    }

    public static HttpResponse of(HttpStatus status, File file) throws IOException {
        return new HttpResponse(status, Files.readAllBytes(file.toPath()))
            .addHeader("Content-Type", Files.probeContentType(file.toPath()) + ";charset=utf-8");
    }

    public static HttpResponse of(HttpStatus status) {
        return new HttpResponse(status, new byte[0]);
    }

    public byte[] getBody() {
        return body;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpResponse addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
}
