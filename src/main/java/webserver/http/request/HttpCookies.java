package webserver.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import util.HttpRequestUtils;
import webserver.http.HttpHeaders;

public class HttpCookies {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookies(HttpHeaders headers) {
        this.cookies.putAll(headers.getHeader("Cookie")
                .map(HttpRequestUtils::parseCookies)
                .orElse(Map.of()));
    }

    public Optional<String> getCookie(String key) {
        return Optional.ofNullable(cookies.get(key));
    }
}
