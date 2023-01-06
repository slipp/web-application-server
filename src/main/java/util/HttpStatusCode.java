package util;

public enum HttpStatusCode {
    OK(200, "OK"), CREATED(201, "CREATED"), FOUND(302, "FOUND"), NOT_FOUND(400,"NOT FOUND");
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
