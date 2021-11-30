package webserver;

import static util.HttpRequestUtils.*;

import controller.UserController;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.getRequestURL;

public class ControllerDispatcher {

    private final HttpRequest httpRequest;

    public ControllerDispatcher(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;

    }

    public void dispatch() {
        Map<String, Object> response = new HashMap<String, Object>();

        String requestUrl = httpRequest.getRequestUri();

        if(requestUrl.equals("/")) {
            response.put("response", "index.html");
        }
        else if(requestUrl.startsWith("/user")) {
            UserController userController = new UserController(httpRequest);
            userController.dispatch();
        }
    }

}
