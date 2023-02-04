package controller;

import model.HttpMethod;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;

public class AbstractController implements Controller{
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod method = HttpMethod.valueOf(httpRequest.getMethod());
        if (method.isGet()) {
            doGet(httpRequest, httpResponse);
        }
        if (method.isPost()) {
            doPost(httpRequest, httpResponse);
        }
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.forward(httpRequest.getUrl());
        httpResponse.response();
    }
}
