package controller;

import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;

public interface Controller {
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
