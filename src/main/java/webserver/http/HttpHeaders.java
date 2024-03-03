package webserver.http;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;

public class HttpHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders() {
    }

    private HttpHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public static HttpHeaders from(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        for (String line = br.readLine(); line != null && !line.isEmpty(); line = br.readLine()) {
            Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
        }
        return new HttpHeaders(headers);
    }

    public Optional<String> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(joining("\r\n", "", "\r\n\r\n"));
    }
}
