package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpRequestUtils;
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
			
			String URL = request.getPath();

			if (URL.equals("/"))
				URL = "/index.html";
			
			if (!URL.contains(".")) {
				if (URL.startsWith("/user/create")) {
					User user = new User(request.getParameter("userId"), request.getParameter("password"),
							request.getParameter("name"), request.getParameter("email"));
					DataBase.addUser(user);
					log.debug("User : {}", user);
					response.sendRedirect("/index.html");
					return;
				} else if (URL.startsWith("/user/login")) {
					User user = DataBase.findUserById(request.getParameter("userId"));
					String redirectURL = null;
					if (user != null && user.getPassword().equals(request.getParameter("password"))) {
						response.addHeader("Set-Cookie", "logined=true");
						redirectURL = "/index.html";
					} else {
						redirectURL = "/user/login_failed.html";
					}
					response.sendRedirect(redirectURL);
				} else if (URL.startsWith("/user/list")) {
					Map<String, String> cookies = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));
					boolean logined = false;
					
					if (cookies.get("logined") != null) {
						logined = Boolean.parseBoolean(cookies.get("logined"));
					}

					if (logined) {
						StringBuilder sb = new StringBuilder();
						Collection<User> users = DataBase.findAll();
						sb.append("<table border='1'>");
						for (User user : users) {
							sb.append("<tr>");
							sb.append("<td>" + user.getUserId() + "</td>");
							sb.append("<td>" + user.getName() + "</td>");
							sb.append("<td>" + user.getEmail() + "</td>");
							sb.append("</tr>");
						}
						sb.append("</table>");
						response.forwardBody(sb.toString());
					} else {
						response.sendRedirect("/index.html");
					}
				}
			} else {
				response.forward(URL);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
