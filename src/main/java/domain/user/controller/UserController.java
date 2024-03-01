package domain.user.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.user.model.User;
import util.HttpRequestUtils;
import webserver.Controller;
import webserver.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.HttpStatus;

public class UserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public HttpResponse controll(HttpRequest request) {
        String requestPath = request.getRequestPath();
        if (requestPath.equals("/user/create") && request.getMethod() == HttpMethod.POST) {
            return signup(request);
        }
        return null;
    }

    private HttpResponse signup(HttpRequest request) {
        Map<String, String> requestBody = request.getRequestBody();
        String userId = requestBody.get("userId");
        String password = requestBody.get("password");
        String name = requestBody.get("name");
        String email = requestBody.get("email");

        User user = User.of(userId, password, name, email);

        log.debug("{} user signup complete", user);

        return HttpResponse.of(HttpStatus.CREATED, user.toString());
    }
}
