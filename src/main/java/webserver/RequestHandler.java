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
import controller.RequestMapping;
import util.HttpRequest;
import util.HttpResponse;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;
	
	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);
			
			Controller controller = RequestMapping.getController(request.getPath());
			
			if(controller == null) {
				String path = getDefaultPath(request.getPath());
				response.forward(path);
			}else {
				controller.service(request, response);
			}
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private String getDefaultPath(String path) {
		if(path.equals("/")) {
			return "/index.html";
		}
		
		return path;
	}
}
