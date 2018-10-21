package util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {
	private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

	private HttpMethod method;
	private String path;
	private String version;
	private Map<String, String> params = new HashMap<>();

	public RequestLine(String requestLine) {
		String[] tokens = requestLine.split(" ");
		
		if(tokens.length != 3) {
			throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
		}
		
		method = HttpMethod.valueOf(tokens[0]);
		version = tokens[2];

		if (method == HttpMethod.POST) {
			path = tokens[1];
			return;
		}

		int index = tokens[1].indexOf('?');
		if (index == -1) {
			path = tokens[1];
		} else {
			path = tokens[1].substring(0, index);
			params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
		}
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getVersion() {
		return version;
	}

	public Map<String, String> getParams() {
		return params;
	}

}
