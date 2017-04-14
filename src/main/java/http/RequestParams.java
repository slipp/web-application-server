package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

public class RequestParams {
	private static final Logger log = LoggerFactory.getLogger(RequestParams.class);
	
	private Map<String, String> params = new HashMap<>();

	public void addQueryString(String queryString) {
		putParams(queryString);
	}

	private void putParams(String data) {
		log.debug("data : {}", data);
		
		if (data == null || data.isEmpty()) {
			return;
		}
		
		params.putAll(HttpRequestUtils.parseQueryString(data));
		log.debug("params : {}", params);
	}

	public void addBody(String body) {
		putParams(body);	
	}

	public String getParameter(String name) {
		return params.get(name);
	}
}
