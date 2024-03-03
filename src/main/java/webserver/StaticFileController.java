package webserver;

import java.io.File;
import java.io.IOException;
import webserver.http.response.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.request.HttpRequest;

public class StaticFileController {

    public static HttpResponse controll(HttpRequest request) throws IOException {
        File file = new File("webapp" + request.getPath());
        return HttpResponse.of(HttpStatus.OK, file);
    }
}
