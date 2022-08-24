package webserver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;

public class RequestLine {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	
	private HttpMethod method;
	private String path;
	private Map<String, String> params = new HashMap<String, String>();
	
	public RequestLine(String requestline) {
		log.debug("request line: {}", requestline); // GET /doc/text.html HTTP/1.1
		String[] tokens = requestline.split(" "); // tokens = [GET, /doc/text.html, HTTP/1.1]
		
		if(tokens.length !=3) {
			throw new IllegalArgumentException(requestline+"이 형식에 맞지 않습니다.");
		}
		//1. HttpMethod의 형태로 method에 담아준다. 
		method = HttpMethod.valueOf(tokens[0]);
		
		
		//2.enum에 isPost 메소드 만들어서 넣어주기 
		if (method.isPost()) {
			path = tokens[1];
			return;
		}
		// get의 경우 url에 파라미터 있으므로 ?를 기준으로 나눠줘야 한다. 
		//ex) GET /user/create?userId=javajigi&password=password&name=JaeSung 
		int index = tokens[1].indexOf("?"); 
		if (index == -1) {
			path = tokens[1];
		} else {
			path = tokens[1].substring(0, index); // /user/create
			//userId=javajigi&password=password&name=JaeSung 
			params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));// index바로 뒤부터 끝까지 가져오기 
		}
	}	
	
	public HttpMethod getMethod() {
		return method;
	}
	public String getPath() {
		return path;
	}
	public Map<String, String> getParams() {
		return params;
	}
	
}
