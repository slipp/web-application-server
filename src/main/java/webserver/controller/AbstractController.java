package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AbstractController implements Controller{
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        switch (httpRequest.getMethod()) {
            case "GET" : doGet(httpRequest, httpResponse);
                break;
            case "POST" : doPost(httpRequest, httpResponse);
                break;
        }
    }
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {}
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {}
}
