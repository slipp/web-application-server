package webserver;

import java.io.IOException;

public interface Controller {
    HttpResponse controll(HttpRequest request) throws IOException;
}