package webserver.user.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import model.User;
import webserver.Controller;
import webserver.HttpRequest;

public class UserController implements Controller {
    @Override
    public byte[] controll(HttpRequest request) throws IOException {
        switch (request.getUri().getRequestPath()) {
            case "/user/create":
                return signup(request);
            case "/user/form.html":
                return signupForm();
            default:
                return null;
        }
    }

    private byte[] signupForm() throws IOException {
        Path path = Paths.get("./webapp/user/form.html");
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        }
        return null;
    }

    private byte[] signup(HttpRequest request) {
        Map<String, String> params = request.getUri().getParams();
        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");

        User user = User.of(userId, password, name, email);

        return user.toString().getBytes();
    }
}
