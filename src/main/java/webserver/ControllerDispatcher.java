package webserver;

import controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            return new HttpResponse(HTTP_STATUS_CODE_200, HTTP_STATUS_MESSAGE_OK, "index.html", CONTENT_TYPE_STRING);
        }

        else if(httpRequest.getHttpMethod().equals(HTTP_METHOD_GET) &&
                ( httpRequest.getHeaders().get("Content-Type") == null) || httpRequest.getHeaders().get("Content-Type").equals(CONTENT_TYPE_STRING) ) {
            return new HttpResponse(HTTP_STATUS_CODE_200, HTTP_STATUS_MESSAGE_OK, requestUri.substring(1), CONTENT_TYPE_STRING);
        }

        else if(requestUri.startsWith("/user")) {
            UserController userController = new UserController(httpRequest);
            return userController.dispatch();
        }

        return new HttpResponse("200", "OK", null, CONTENT_TYPE_STRING); // 어떻게 처리해야되는걸까?
    }
}
