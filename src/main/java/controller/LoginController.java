package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;

public class LoginController extends AbstractController{
    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        boolean loginSuccess = false;
        String userId = httpRequest.getQueryString().get("userId");
        String password = httpRequest.getQueryString().get("password");
        System.out.println("login User : " + userId + " Password : " + password);

        User registeredUser = DataBase.findUserById(userId);
        if ( registeredUser.getPassword().equals(password) ) {
            loginSuccess = true;
            System.out.println("login Success!!");
        }

        if( !loginSuccess ) {
            httpResponse.sendRedirect("/user/login_failed.html");
        }

        if( loginSuccess ) {
            httpResponse.sendRedirect("/index.html");
            httpResponse.addHeader("Set-Cookie", "logined=true");
        }
        httpResponse.response();
    }
}
