package webserver;

import java.util.Map;

import util.HttpRequestUtils;

public class Uri {
    private String uri;
    private String requestPath;
    private Map<String, String> params;

    private Uri(String uri) {
        this.uri = uri;
        int index = uri.indexOf("?");
        if (index >= 0) {
            this.requestPath = uri.substring(0, index);
            this.params = HttpRequestUtils.parseQueryString(uri.substring(index + 1));
        } else {
            this.requestPath = uri;
        }
    }

    public static Uri from(String uri) {
        return new Uri(uri);
    }

    public String getRequestPath() {
        return this.requestPath;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getUri() {
        return this.uri;
    }
}
