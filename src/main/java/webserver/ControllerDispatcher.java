package webserver;

import static util.HttpRequestUtils.*;
import static common.ParameterConstants.*;

import controller.UserController;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.getRequestURL;

public class ControllerDispatcher {

    private final HttpRequest httpRequest;

    public ControllerDispatcher(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;

    }

    public HttpResponse dispatch() {
        Map<String, Object> response = new HashMap<String, Object>();

        String requestUrl = httpRequest.getRequestUri();

        if(requestUrl.startsWith("/user")) {
            UserController userController = new UserController(httpRequest);
            return userController.dispatch();
        }

        return new HttpResponse("200", "OK", null, CONTENT_TYPE_STRING);
    }
}
