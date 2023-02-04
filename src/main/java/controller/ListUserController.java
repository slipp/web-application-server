package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;
import java.util.Collection;

public class ListUserController extends AbstractController{

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String cookie = httpRequest.getHeader().get("Cookie");
        if(isLogin(cookie)) {
            StringBuilder sb = new StringBuilder();
            Collection<User> userList = (Collection<User>) DataBase.findAll();
            for(User item : userList) {
                sb.append("<h3>");
                sb.append(item.getUserId());
                sb.append(" : ");
                sb.append(item.getName());
                sb.append("</h3>");
            }
            System.out.println("list : " + sb.toString());
            httpResponse.forward("");
            httpResponse.setBody(sb.toString().getBytes());
            httpResponse.response();
        } else {
            httpResponse.sendRedirect("/user/login_failed.html");
        }
    }

    private boolean isLogin(String cookie) {
        if (cookie == null) return false;
        if (cookie.equals("logined=true")) return true;
        return false;
    }
}
