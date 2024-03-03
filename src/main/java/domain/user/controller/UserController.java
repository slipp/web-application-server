package domain.user.controller;

import domain.user.model.User;
import domain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.Controller;
import webserver.http.HttpMethod;
import webserver.http.HttpStatus;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

public class UserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public HttpResponse controll(HttpRequest request) {
        String requestPath = request.getPath();
        if (requestPath.equals("/user/create") && request.getMethod() == HttpMethod.POST) {
            return signup(request);
        }
        if (requestPath.equals("/user/login") && request.getMethod() == HttpMethod.POST) {
            return login(request);
        }
        if (requestPath.equals("/user/list") && request.getMethod() == HttpMethod.GET) {
            return getUserList(request);
        }
        return null;
    }

    private HttpResponse signup(HttpRequest request) {
        try {
            String userId = request.getBody().getParameter("userId");
            String password = request.getBody().getParameter("password");
            String name = request.getBody().getParameter("name");
            String email = request.getBody().getParameter("email");
            User user = UserService.signup(userId, password, name, email);
            return HttpResponse.of(HttpStatus.FOUND, user.toString())
                .addHeader("Location", "/index.html");
        } catch (IllegalArgumentException e) {
            return HttpResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private HttpResponse login(HttpRequest request) {
        try {
            String userId = request.getBody().getParameter("userId");
            String password = request.getBody().getParameter("password");
            if (UserService.login(userId, password)) {
                return HttpResponse.of(HttpStatus.FOUND)
                    .addHeader("Location", "/index.html")
                    .addHeader("Set-Cookie", "logined=true");
            }
            return HttpResponse.of(HttpStatus.FOUND)
                .addHeader("Location", "/user/login_failed.html")
                .addHeader("Set-Cookie", "logined=false");
        } catch (IllegalArgumentException e) {
            return HttpResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private HttpResponse getUserList(HttpRequest request) {
        boolean logined = request.getCookie("logined")
            .map("true"::equals)
            .orElse(false);
        if (logined) {
            return HttpResponse.of(HttpStatus.OK, UserService.getUserList().toString());
        }
        return HttpResponse.of(HttpStatus.FOUND).addHeader("Location", "/user/login.html");
    }
}
