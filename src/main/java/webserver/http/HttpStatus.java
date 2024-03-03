package webserver.http;

public enum HttpStatus {
    OK(200, "OK"), 
    CREATED(201, "Created"), 
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String value;

    HttpStatus(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int code() {
        return code;
    }

    public String value() {
        return value;
    }
}
