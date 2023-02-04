package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

public class CreateUserController extends AbstractController{
    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String userId = httpRequest.getQueryString().get("userId");
        String password = httpRequest.getQueryString().get("password");
        String name = httpRequest.getQueryString().get("name");
        String email = httpRequest.getQueryString().get("email");

        User user = new User(userId, password, name, email);
        System.out.println(user.toString());

        DataBase.addUser(user);

        httpResponse.sendRedirect("/index.html");
        httpResponse.response();
    }
}
