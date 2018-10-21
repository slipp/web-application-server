package util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

	private Map<String, String> headers;
	private DataOutputStream dos;

	public HttpResponse(OutputStream os) {
		headers = new HashMap<>();
		dos = new DataOutputStream(os);
	}

	public void addHeader(String param, String value) {
		headers.put(param, value);
	}

	public void forward(String forwardURL) {
		String format = forwardURL.substring(forwardURL.lastIndexOf('.') + 1);

		addHeader("Content-Type", "text/" + format);
		processHeaders("HTTP/1.1 200 OK");
		responseBody(readFile(forwardURL));
	}

	public void forwardBody(String body) {
		response200Header(body.getBytes().length);
		responseBody(body.getBytes());
	}

	public void sendRedirect(String redirectURL) {
		addHeader("Location", "http://localhost:8080" + redirectURL);
		processHeaders("HTTP/1.1 302 Found");
	}

	public void response200Header(int lengthOfBodyContent) {
		addHeader("Content-Type", "text/html;charset=utf-8");
		processHeaders("HTTP/1.1 200 OK");
	}

	public void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void processHeaders(String responseLine) {
		try {
			dos.writeBytes(responseLine + "\r\n");
			for (String key : headers.keySet()) {
				dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
			}
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private byte[] readFile(String filePath) {
		try {
			return Files.readAllBytes(Paths.get("webapp" + filePath));
		} catch (IOException e) {
			log.error("Exception occur while reading a file at {}", filePath);
			return null;
		}
	}
}
