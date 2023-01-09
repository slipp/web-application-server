package controller;

import db.DataBase;
import model.User;
import util.*;
import util.HttpRequestUtils.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController implements Controller {
    public void get(String path, BufferedReader in, DataOutputStream out) throws IOException {
        HttpResponseUtils.responseHeader(out, HttpVersion.HTTP_V1,HttpStatusCode.NOT_FOUND, new ArrayList<>());
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
        HttpResponseUtils.responseHeader(out,HttpVersion.HTTP_V1, HttpStatusCode.FOUND, headers);
    }
}
