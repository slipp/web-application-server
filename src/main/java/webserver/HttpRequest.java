package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private String method;
	private String path;
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();

	public HttpRequest(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();

			if (line == null) {
				return;
			}

			processRequestLine(line);

			while (line.equals("")) {
				log.debug("header line: {}", line);
				line = br.readLine();
				String[] tokens = line.split(":");
				headers.put(tokens[0].trim(), tokens[1].trim());

			}
			if("POST".equals(method)) {
				String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
				params = HttpRequestUtils.parseQueryString(body);
				
			}
		} catch (IOException io) {
			log.error(io.getMessage());
		}

	}

	private void processRequestLine(String requestline) {
		log.debug("request line: {}", requestline); // GET /doc/text.html HTTP/1.1
		String[] tokens = requestline.split(" ");
		method = tokens[0]; // GET

		if ("POST".equals(method)) {
			path = tokens[1];
			return;
		}
		int index = tokens[1].indexOf("?");
		if (index == -1) {
			path = tokens[1];
		} else {
			path = tokens[1].substring(0, index); // 잘라내고 싶은 범위 정하기 (0부터 index까지)
			params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));// 파라미터 받아오기

		}

	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public String getParameter(String name) {
		return params.get(name);
	}
}
