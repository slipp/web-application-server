package webserver;

import common.ParameterConstants;

import java.util.Map;

public class HttpResponse {

    private final String httpVersion = ParameterConstants.HTTP_VERSION;
    private final String status;
    private final String message;
    private final String body;

    /* header */
    private final String Content_Type;
    private String location;
    private String cookies;

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

    public String getBody() {
        return body;
    }

    public String getContent_Type() { return Content_Type; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "httpVersion='" + httpVersion + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
