package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
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
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String[] requestHeader = br.readLine().split(" ");
			
			Map<String, String> header = new HashMap<>();
			String line = null;
			while (!(line = br.readLine()).equals("")) {
				header.put(line.substring(0, line.indexOf(':')), line.substring(line.indexOf(':') + 1));
			}
			
			String method = requestHeader[0];
			String version = requestHeader[2];

			int index = requestHeader[1].indexOf('?');
			String URL = requestHeader[1];
			String params = null;
			
			if(URL.equals("/")) URL = "/index.html";
			
			Map<String, String> queries = null;
			if(index != -1) {
				URL = requestHeader[1].substring(0, index);
				params = requestHeader[1].substring(index + 1);
				queries = HttpRequestUtils.parseQueryString(params);
			}
			
			if(URL.startsWith("/user/create")) {
				User user = new User(queries.get("userId"), queries.get("password"), queries.get("name"), queries.get("email"));
				log.debug("user : {}", user);
			}
			
			DataOutputStream dos = new DataOutputStream(out);

			String filePath = "webapp" + URL;
			log.info("filepath : {}", filePath);
			byte[] body = Files.readAllBytes(Paths.get(filePath));

			response200Header(dos, body.length);
			responseBody(dos, body);
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
