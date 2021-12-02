package webserver;

import controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static common.ParameterConstants.*;

public class ControllerDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ControllerDispatcher.class);

    private final HttpRequest httpRequest;

    public ControllerDispatcher(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponse dispatch() {
        String requestUri = httpRequest.getRequestUri();
        log.debug("requestURi: {}", requestUri);

        if(requestUri.equals("/")) {
            return new HttpResponse(HTTP_STATUS_CODE_200, HTTP_STATUS_MESSAGE_OK, "index.html", CONTENT_TYPE_TEXT_HTML);
        }

        else if(httpRequest.getHttpMethod().equals(HTTP_METHOD_GET) &&
                httpRequest.getRequestUri().split("\\.").length > 1){
            log.debug("{}", "static!!!!!!!");
            return new HttpResponse(HTTP_STATUS_CODE_200, HTTP_STATUS_MESSAGE_OK, requestUri.substring(1), CONTENT_TYPE_TEXT_HTML);
        }

        else if(requestUri.startsWith("/user")) {
            UserController userController = new UserController(httpRequest);
            return userController.dispatch();
        }

        return new HttpResponse("200", "OK", null, CONTENT_TYPE_TEXT_HTML); // 어떻게 처리해야되는걸까?
    }
}
