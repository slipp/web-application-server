package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils.Pair;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private Map<String, String> header = new HashMap<>();
	private Map<String, String> params = new HashMap<>();
	private RequestLine requestLine;

	public HttpRequest(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String line = null;
			line = br.readLine();

			if (line == null) {
				return;
			}

			requestLine = new RequestLine(line);

			while (!(line = br.readLine()).equals("")) {
				Pair pair = HttpRequestUtils.parseHeader(line);
				header.put(pair.getKey().trim(), pair.getValue().trim());
			}

			if (getMethod().isPost()) {
				String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
				params = HttpRequestUtils.parseQueryString(body);
			} else {
				params = requestLine.getParams();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String getVersion() {
		return requestLine.getVersion();
	}

	public String getHeader(String headerName) {
		return header.get(headerName);
	}

	public String getParameter(String paramName) {
		return params.get(paramName);
	}
}
