package webserver;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {
    Map<String, String> requestHeaders = new HashMap<>();
    
    public void add(String headerLine) {
        requestHeaders.put(HttpRequestUtils.parseHeader(headerLine).getKey(),HttpRequestUtils.parseHeader(headerLine).getValue());
    }

    public String getHeader(String key) {
        return this.requestHeaders.get(key);
    }
}
