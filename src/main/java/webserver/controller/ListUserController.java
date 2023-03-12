package webserver.controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Collection;

public class ListUserController extends AbstractController {
    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (isLogin(httpRequest.getParameter("Cookie"))) {
            // 로그인 한 상태
            int idx = 3;

            Collection<User> userList = DataBase.findAll();

            StringBuilder sb = new StringBuilder();
            for(User user : userList) {
                sb.append("<tr>");
                sb.append("<th scope=\"row\">"+idx+"</th><td>"+user.getUserId()+"</td> <td>"+user.getName()+"</td> <td>"+user.getEmail()+"</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td></tr>");
                idx++;
            }

            String fileData = new String(Files.readAllBytes(new File("./webapp" + httpRequest.getPath()).toPath()));
            fileData = fileData.replace("%user_list%", URLDecoder.decode(sb.toString(), "UTF-8"));

            byte[] body = fileData.getBytes();
            httpResponse.forward(body);
        } else {
            httpResponse.sendRedirect("/index.html");
        }
    }

    private boolean isLogin(String str) {
        return str.contains("logined=true");
    }
}
