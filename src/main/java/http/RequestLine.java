package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

class RequestLine {
    private HttpMethod method;
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
    private String path;
    private Map<String, String> params = new HashMap<>();

    RequestLine(String requestLine) {
        log.debug("request line : {}", requestLine);
        String[] tokens = requestLine.split(" ");
        method = HttpMethod.valueOf(tokens[0]);
        if (method == HttpMethod.POST) {
            path = tokens[1];
            return;
        }
        int index = tokens[1].indexOf("?");
        if (index == -1) {
            path = tokens[1];
        } else {
            path = tokens[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
        }
    }

    HttpMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    Map<String, String> getParams() {
        return params;
    }
}
