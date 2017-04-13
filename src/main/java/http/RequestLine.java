package http;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private String method;

    private String path;

    private Map<String, String> params;

    RequestLine(String requestLine) {
    }

    String getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    Map<String, String> getParams() {
        return params;
    }
}
