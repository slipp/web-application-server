package webserver;

import static util.HttpRequestUtils.*;

import controller.UserController;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.getRequestURL;

public class ControllerDispatcher {

    private final String requestLine;

    public ControllerDispatcher(String requestLine) {
        this.requestLine = requestLine;

    }

    public void dispatch() {
        String requestUrl = getRequestURL(this.requestLine);
        String httpMethod = getHttpMethod(this.requestLine);

        Map<String, Object> response = new HashMap<String, Object>();

        if(requestUrl.equals("/")) {
            response.put("response", "index.html");
        }
        else if(requestUrl.startsWith("/user")) {
            UserController userController = new UserController(requestUrl, httpMethod);
            userController.dispatch();
        }
    }

}
