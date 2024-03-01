package webserver;

public enum HttpStatus {
    OK(200, "OK"), 
    CREATED(201, "Created"), 
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

    private int code;
    private String value;

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
