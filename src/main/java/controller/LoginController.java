package controller;

import db.DataBase;
import model.User;
import util.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginController implements Controller {
    @Override
    public void get(String path, BufferedReader in, DataOutputStream out) throws IOException {
        HttpResponseUtils.responseHeader(out, HttpStatusCode.NOT_FOUND, new ArrayList<>());
    }

    @Override
    public void post(String path, BufferedReader in, DataOutputStream out) throws IOException {
        // extract & parse request headers
        Map<String, String> requestHeaders = new HashMap<>();
        String line = in.readLine();
        while (!"".equals(line)) {
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            requestHeaders.put(pair.getKey(), pair.getValue());
            line = in.readLine();
        }


        // extract & parse body
        int contentLength = Integer.valueOf(requestHeaders.get("Content-Length"));
        String requestBody = IOUtils.readData(in, contentLength);
        Map<String, String> loginForm = new HashMap<>();
        String[] keyValues = requestBody.split("&");
        for (String keyValue : keyValues) {
            HttpRequestUtils.Pair pair = HttpRequestUtils.getKeyValue(keyValue, "=");
            loginForm.put(pair.getKey(), pair.getValue());
        }

        // validate user
        String userId = loginForm.get("userId");
        String password = loginForm.get("password");
        User user = DataBase.findUserById(userId);
        if (user == null) {
            HttpResponseUtils.responseHeader(out, HttpStatusCode.BAD_REQUEST, new ArrayList<>());
            String errorMessage = "User not found!";
            HttpResponseUtils.responseBody(out, errorMessage.getBytes());
            return;
        }
        if (!user.getPassword().equals(password)) {
            HttpResponseUtils.responseHeader(out, HttpStatusCode.BAD_REQUEST, new ArrayList<>());
            String errorMessage = "Invalid email or password";
            HttpResponseUtils.responseBody(out, errorMessage.getBytes());
            return;
        }
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Set-Cookie", "logined=true"));
        headers.add(new Header("Content-Type", "text/html;charset=utf-8"));
        headers.add(new Header("Location", "http://localhost:8080/index.html"));
        HttpResponseUtils.responseHeader(out, HttpStatusCode.FOUND, headers);



    }
}
