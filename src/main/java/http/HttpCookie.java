package http;

import java.util.HashMap;
import java.util.Map;

import util.HttpRequestUtils;

public class HttpCookie {
	private Map<String, String> cookies;
	
	public HttpCookie(String cookieValues) {
		if (cookieValues == null) {
			cookies = new HashMap<String, String>();
			return;
		}
		
		cookies = HttpRequestUtils.parseCookies(cookieValues);
	}
	
	public String getCookie(String name) {
		return cookies.get(name);
	}
}
