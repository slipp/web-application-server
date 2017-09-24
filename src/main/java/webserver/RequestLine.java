package webserver;

class RequestLine {
    private String requestLine;

    private RequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    static RequestLine of(String requestLine) {
        return new RequestLine(requestLine);
    }

    String getRequestResource() {
        return requestLine.split(" ")[1];
    }
}
