package webserver;

class RequestLine {
    private String requestLine;
    private String requestUri;
    private String requestResource;
    private String requestParam;

    private RequestLine(String requestLine) {
        this.requestLine = requestLine;
        this.requestUri = this.requestLine.split(" ")[1];
        this.requestResource = this.requestUri.split("\\?")[0];
        this.requestParam = extractParams();
    }

    private String extractParams() {
        if (hasParams()) {
            return this.requestUri.split("\\?")[1];
        }

        return "";
    }

    private boolean hasParams() {
        return this.requestUri.split("\\?").length > 1;
    }

    static RequestLine of(String requestLine) {
        return new RequestLine(requestLine);
    }

    public String getRequestResource() {
        return requestResource;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public String getRequestUri() {
        return requestUri;
    }
}
