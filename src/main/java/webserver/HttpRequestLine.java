package webserver;

public class HttpRequestLine {
    
    public static String getPath(String requestLine) {
        return requestLine.split(" ")[1];
    }
}
