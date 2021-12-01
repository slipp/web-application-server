package controller;

import static common.ParameterConstants.*;

import controller.dto.UserCreateRequestDto;
import service.UserService;
import util.HttpRequestUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class UserController {

    private final HttpRequest httpRequest;
    private UserService userService = new UserService();

    public UserController(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponse dispatch() {
        Map<String, Object> response = new HashMap<String, Object>();

        String requestUri = httpRequest.getRequestUri();
        String httpMethod = httpRequest.getHttpMethod();
        String body       = httpRequest.getBody();

        if(requestUri.startsWith("/user/create")&&httpMethod.equals("POST")) {
            return post_create(body);
        }

        return null;
    }

    private HttpResponse post_create(String body) {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(body);

        UserCreateRequestDto userCreateRequestDto =
                new UserCreateRequestDto(parameters.get("userId"), parameters.get("password"),
                                         parameters.get("name"), parameters.get("email"));

        userService.create(userCreateRequestDto);

        HttpResponse httpResponse = new HttpResponse(HTTP_STATUS_CODE_302, HTTP_STATUS_MESSAGE_FOUND, PATH_HOME, CONTENT_TYPE_STRING);

        return httpResponse;
    }
}
