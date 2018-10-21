package util;

public enum HttpMethod {
	GET,
	POST;
	
	public boolean isPost() {
		return this == POST;
	}
}
