package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;
import util.HttpRequest;
import util.HttpResponse;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;
	private Map<String, Controller> controllers;
	
	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
		this.controllers = new HashMap<>();
		generateController();
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);
			
			String URL = request.getPath();

			if (URL.equals("/"))
				URL = "/index.html";
			
			if(URL.contains(".")) response.forward(URL);
			else controllers.get(URL).service(request, response);
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void generateController() {
		controllers.put("/user/create", new CreateUserController());
		controllers.put("/user/login", new LoginController());
		controllers.put("/user/list", new ListUserController());
	}
}
