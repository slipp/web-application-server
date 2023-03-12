package webserver.controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class CreateUserController extends AbstractController {
    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        DataBase.addUser(new User(
                httpRequest.getParameter("userId"),
            httpRequest.getParameter("password"),
            httpRequest.getParameter("name"),
            httpRequest.getParameter("email")
        ));

        httpResponse.sendRedirect("/index.html");
    }
}
