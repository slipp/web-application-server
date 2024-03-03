package webserver;

import java.io.IOException;
import webserver.http.response.HttpResponse;
import webserver.http.request.HttpRequest;

public interface Controller {
    HttpResponse controll(HttpRequest request) throws IOException;
}