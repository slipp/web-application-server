package webserver.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import util.HttpRequestUtils;
import util.IOUtils;
import webserver.http.HttpHeaders;

public class HttpRequestBody {
    private final Map<String, String> parameters = new HashMap<>();

    private HttpRequestBody(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public static HttpRequestBody from(BufferedReader br, HttpHeaders headers) throws IOException {
        int contentLength = Integer.parseInt(headers.getHeader("Content-Length").orElse("0"));
        String data = IOUtils.readData(br, contentLength);
        String contentType = headers.getHeader("Content-Type").orElse("");
        Map<String, String> parameters = new HashMap<>();
        if ("application/x-www-form-urlencoded".equals(contentType)) {
            parameters.putAll(HttpRequestUtils.parseQueryString(data));
        }
        return new HttpRequestBody(parameters);
    }

    public String getParameter(String key) {
        if (!parameters.containsKey(key)) {
            throw new IllegalArgumentException("No such parameter: " + key + " in request body.");
        }
        return parameters.get(key);
    }
}
