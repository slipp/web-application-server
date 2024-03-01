package webserver;

import java.io.File;
import java.io.IOException;

public class StaticFileController {

    public static HttpResponse controll(HttpRequest request) throws IOException {
        File file = new File("webapp" + request.getRequestPath());
        return HttpResponse.of(HttpStatus.OK, file);
    }
}
