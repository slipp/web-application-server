package domain.user.controller;

import domain.user.model.User;
import domain.user.service.UserService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        if (requestPath.equals("/user/login") && request.getMethod() == HttpMethod.POST) {
            return login(request);
        }
        if (requestPath.equals("/user/list") && request.getMethod() == HttpMethod.GET) {
            return getUserList(request);
        }
        return null;
    }

    private HttpResponse signup(HttpRequest request) {
        Map<String, String> requestBody = request.getRequestBody();
        String userId = requestBody.get("userId");
        String password = requestBody.get("password");
        String name = requestBody.get("name");
        String email = requestBody.get("email");
        User user = UserService.signup(userId, password, name, email);
        return HttpResponse.of(HttpStatus.FOUND, user.toString())
            .addHeader("Location", "/index.html");
    }

    private HttpResponse login(HttpRequest request) {
        Map<String, String> requestBody = request.getRequestBody();
        String userId = requestBody.get("userId");
        String password = requestBody.get("password");
        if (UserService.login(userId, password)) {
            return HttpResponse.of(HttpStatus.FOUND)
                .addHeader("Location", "/index.html")
                .addHeader("Set-Cookie", "logined=true");
        }
        return HttpResponse.of(HttpStatus.FOUND)
            .addHeader("Location", "/user/login_failed.html")
            .addHeader("Set-Cookie", "logined=false");
    }

    private HttpResponse getUserList(HttpRequest request) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(request.getHeaders().get("Cookie"));
        if ("true".equals(cookies.get("logined"))) {
            return HttpResponse.of(HttpStatus.OK, UserService.getUserList().toString());
        }
        return HttpResponse.of(HttpStatus.FOUND).addHeader("Location", "/user/login.html");
    }
}
