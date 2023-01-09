package controller;

import db.DataBase;
import model.User;
import util.*;
import util.HttpRequestUtils.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class UserController implements Controller {


    //
    public void get(String path, BufferedReader in, DataOutputStream out) throws IOException {
        // extract & parse request headers
        Map<String, String> requestHeaders = new HashMap<>();
        boolean isLogin = false;

        String headerLine = in.readLine();
        while (!"".equals(headerLine)) {
            if(headerLine.contains("Cookie")){
                isLogin = HttpRequestUtils.isLogin(headerLine);
            }
            Pair pair = HttpRequestUtils.parseHeader(headerLine);
            requestHeaders.put(pair.getKey(), pair.getValue());
            headerLine = in.readLine();
        }

        // if not logined -> redirect to user/login.html
        if (!isLogin) {
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Set-Cookie", "logined=true;path=/"));
            headers.add(new Header("Content-Type", "text/html;charset=utf-8"));
            headers.add(new Header("Location", "http://localhost:8080/index.html"));
            headers.forEach(System.out::println);
            HttpResponseUtils.responseHeader(out, HttpVersion.HTTP_V1, HttpStatusCode.FOUND, headers);
        }

        // find user list
        Collection<User> users = DataBase.findAll();

        // create response body
        StringBuffer sb = new StringBuffer();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        byte [] body = sb.toString().getBytes();


        // send response
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Content-Type", "text/html"+";charset=utf-8"));
        headers.add(new Header("Content-Length", String.valueOf(body.length)));
        HttpResponseUtils.responseHeader(out, HttpVersion.HTTP_V1, HttpStatusCode.OK, headers);
        HttpResponseUtils.responseBody(out, body);
    }

    public void post(String path, BufferedReader in, DataOutputStream out) throws IOException {
        // extract & parse request headers
        Map<String, String> requestHeaders = new HashMap<>();
        String headerLine = in.readLine();
        while (!"".equals(headerLine)) {
            Pair pair = HttpRequestUtils.parseHeader(headerLine);
            requestHeaders.put(pair.getKey(), pair.getValue());
            headerLine = in.readLine();
        }


        // extract & parse body
        int contentLength = Integer.valueOf(requestHeaders.get("Content-Length"));
        String requestBody = IOUtils.readData(in, contentLength);
        Map<String, String> signUpForm = new HashMap<>();
        String[] keyValues = requestBody.split("&");
        for (String keyValue : keyValues) {
            Pair pair = HttpRequestUtils.getKeyValue(keyValue, "=");
            signUpForm.put(pair.getKey(), pair.getValue());
        }

        // save user
        User user = new User(signUpForm.get("userId"), signUpForm.get("password"), signUpForm.get("name"), signUpForm.get("email"));
        DataBase.addUser(user);

        // redirect to index.html
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Location", "http://localhost:8080/user/login.html"));
        HttpResponseUtils.responseHeader(out, HttpVersion.HTTP_V1, HttpStatusCode.FOUND, headers);
        System.out.println("sign up completed!");
    }
}
