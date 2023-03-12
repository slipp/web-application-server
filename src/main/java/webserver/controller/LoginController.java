package webserver.controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Objects;

public class LoginController extends AbstractController{
    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        User user = DataBase.findUserById(httpRequest.getParameter("userId"));
        String inputPassword = httpRequest.getParameter("password");

        if (user != null && Objects.equals(user.getPassword(), inputPassword)) {
            // 로그인 성공
            httpResponse.sendRedirect("/index.html");
            httpResponse.addHeader("Cookie","logined=true");
        } else {
            // 로그인 실패
            httpResponse.addHeader("Cookie","logined=false");
            httpResponse.sendRedirect("/user/login_failed.html");
        }
    }
}
