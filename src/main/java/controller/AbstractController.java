package controller;

import util.HttpRequest;
import util.HttpResponse;

abstract public class AbstractController implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		String method = request.getMethod();
		if(method.equals("GET")) {
			doGet(request, response);
		}else if(method.equals("POST")) {
			doPost(request, response);
		}
	}

	public void doPost(HttpRequest request, HttpResponse response) {
	}
	
	public void doGet(HttpRequest request, HttpResponse response) {
	}
}
