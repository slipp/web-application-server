package webserver;

import java.io.IOException;

public interface Controller {
    byte[] controll(HttpRequest request) throws IOException;
}