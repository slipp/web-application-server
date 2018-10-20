package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpRequestUtils;

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

			String URL = request.getPath();

			if (URL.equals("/"))
				URL = "/index.html";

			DataOutputStream dos = new DataOutputStream(out);
			byte[] body = null;
			String fileFormat = URL.substring(URL.lastIndexOf('.') + 1);

			if (fileFormat.equals(URL)) {
				if (URL.startsWith("/user/create")) {
					User user = new User(request.getParameter("userId"), request.getParameter("password"),
							request.getParameter("name"), request.getParameter("email"));
					DataBase.addUser(user);
					log.debug("User : {}", user);
					response302Header(dos, "/index.html");
					return;
				} else if (URL.startsWith("/user/login")) {
					User user = DataBase.findUserById(request.getParameter("userId"));
					String logined = null;
					if (user != null && user.getPassword().equals(request.getParameter("password"))) {
						log.debug("login success");
						logined = "true";
						URL = "/index.html";
					} else {
						log.debug("login failed");
						logined = "false";
						URL = "/user/login_failed.html";
					}
					body = readFile(URL);
					response302HeaderWithCookie(dos, "logined", logined, URL);
					responseBody(dos, body);
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
						body = sb.toString().getBytes();
					} else {
						URL = "/index.html";
						body = readFile(URL);
					}
					response200Header(dos, body.length);
					responseBody(dos, body);
				}
			} else {
				if (!fileFormat.equals(URL) && !fileFormat.equals("html")) {
					body = readFile(URL);
					responseNonHTMLContentType(dos, fileFormat);
					responseBody(dos, body);
				} else {
					body = readFile(URL);
					response200Header(dos, body.length);
					responseBody(dos, body);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private byte[] readFile(String filePath) {
		try {
			return Files.readAllBytes(Paths.get("webapp" + filePath));
		} catch (IOException e) {
			log.error("exception occur while reading a file at {}", filePath);
			return null;
		}
	}

	private void responseNonHTMLContentType(DataOutputStream dos, String type) {
		String format = null;
		if (type.equals("css"))
			format = "css";
		else if (type.equals("js"))
			format = "javascript";

		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/" + format + " \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302HeaderWithCookie(DataOutputStream dos, String cookieParam, String cookieValue,
			String location) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
			dos.writeBytes("Location: http://localhost:8080" + location + "\r\n");
			dos.writeBytes("Set-Cookie: " + cookieParam + "=" + cookieValue);
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos, String location) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: http://localhost:8080" + location + "\r\n");
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
