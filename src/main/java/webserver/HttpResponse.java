package webserver;

import common.ParameterConstants;

import java.util.Map;

public class HttpResponse {

    private final String httpVersion = ParameterConstants.HTTP_VERSION;
    private final String status;
    private final String message;
    private final String body;
    private Map<String, String> headers;
    private final String Content_Type;

    public HttpResponse(String status, String message, String body, String Content_Type) {
        this.status = status;
        this.message = message;
        this.body = body;
        this.Content_Type = Content_Type;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public String getBody() {
        return body;
    }

    public String getContent_Type() { return Content_Type; }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "httpVersion='" + httpVersion + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", body='" + body + '\'' +
                ", headers=" + headers +
                '}';
    }
}
