package controller;

import util.HttpRequest;
import util.HttpResponse;

public interface Controller {
	void service(HttpRequest request, HttpResponse response);
}
