package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

class RequestParams {
	private static final Logger log = LoggerFactory.getLogger(RequestParams.class);
	
	private Map<String, String> params = new HashMap<>();

	void addQueryString(String queryString) {
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

	void addBody(String body) {
		putParams(body);	
	}

	String getParameter(String name) {
		return params.get(name);
	}
}
