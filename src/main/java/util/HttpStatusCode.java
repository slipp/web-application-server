package util;

public enum HttpStatusCode {
    OK(200, "OK"), CREATED(201, "CREATED"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "BAD REQUEST"), NOT_FOUND(404, "NOT FOUND");
    private int code;
    private String message;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
