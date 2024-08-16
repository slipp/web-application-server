package http;

import util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;
    
    HttpCookie(String cookieValue) {
        cookies = HttpRequestUtils.parseCookies(cookieValue);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
