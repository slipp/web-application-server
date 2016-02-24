package webserver;

import http.HttpCookie;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;

import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);
			String path = getDefaultPath(request.getPath());
			
			if ("/create".equals(path)) {
				User user = new User(
						request.getParameter("userId"), request.getParameter("password"), 
						request.getParameter("name"), request.getParameter("email"));
				log.debug("user : {}", user);
				DataBase.addUser(user);
				response.sendRedirect("/index.html");
			} else if ("/login".equals(path)) {
				User user = DataBase.findUserById(request.getParameter("userId"));
				if (user != null) {
					if (user.login(request.getParameter("password"))) {
						response.addHeader("Set-Cookie", "logined=true");
						response.sendRedirect("/index.html");
					} else {
						response.sendRedirect("/user/login_failed.html");
					}
				} else {
					response.sendRedirect("/user/login_failed.html");
				}
			} else if ("/user/list".equals(path)) {
				HttpCookie cookies = request.getCookies();
				if (!isLogin(cookies)) {
					response.sendRedirect("/user/login.html");
					return;
				}

				Collection<User> users = DataBase.findAll();
				StringBuilder sb = new StringBuilder();
				for (User user : users) {
					sb.append(user.getUserId() + " : " + user.getName() + " : " + user.getEmail() + "<br/>");
				}
				response.forwardBody(sb.toString());
			} else {
				response.forward(path);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private boolean isLogin(HttpCookie cookie) {
		String value = cookie.getCookie("logined");
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	private String getDefaultPath(String path) {
		if (path.equals("/")) {
			return "/index.html";
		}
		return path;
	}
}
