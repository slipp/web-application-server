package webserver;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String requestLine;
    private final String httpMethod;
    private final String requestUri;
    private final String body;
    private final Map<String, String> headers;

    public HttpRequest(BufferedReader br) throws IOException {
        this.requestLine = br.readLine();
        this.httpMethod = HttpRequestUtils.getHttpMethod(requestLine);
        this.requestUri = HttpRequestUtils.getRequestURL(requestLine);
        this.headers = new HashMap<String, String>();

        while(true) {
            String header = br.readLine();
            if(header == null || "".equals(header)) break;
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(header);
            this.headers.put(pair.getKey(), pair.getValue());
        }
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length","0"));
        this.body = contentLength > 0 ? IOUtils.readData(br, contentLength) : "";
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine='" + requestLine + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", body='" + body + '\'' +
                ", headers=" + headers +
                '}';
    }
}
