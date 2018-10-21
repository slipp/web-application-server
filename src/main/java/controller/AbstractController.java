package controller;

import util.HttpMethod;
import util.HttpRequest;
import util.HttpResponse;

abstract public class AbstractController implements Controller {

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		HttpMethod method = request.getMethod();
		if(method == HttpMethod.GET) {
			doGet(request, response);
		}else if(method == HttpMethod.POST) {
			doPost(request, response);
		}
	}

	public void doPost(HttpRequest request, HttpResponse response) {
	}
	
	public void doGet(HttpRequest request, HttpResponse response) {
	}
}
