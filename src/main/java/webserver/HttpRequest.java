package webserver;
import java.util.Map;

public interface HttpRequest {
    RequestLine getRequestLine();

    RequestHeader getRequestHeader();

    Map<String, String> getRequestBody();
}
