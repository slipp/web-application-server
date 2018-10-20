package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import util.HttpRequestUtils.Pair;

public class HttpRequest {
	private String method;
	private String path;
	private String version;
	private Map<String, String> header;
	private Map<String, String> params;

	public HttpRequest(InputStream in) throws IOException {
		header = new HashMap<>();
		params = new HashMap<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		
		String line = null;
		line = br.readLine();
		
		if(line == null) {
			return;
		}
		
		String[] requestLine = line.split(" ");
		
		method = requestLine[0];
		path = requestLine[1];
		version = requestLine[2];
		
		while(!(line = br.readLine()).equals("")) {
			Pair pair = HttpRequestUtils.parseHeader(line);
			header.put(pair.getKey(), pair.getValue());
		}

		String queryString = null;
		if(method.equals("GET")) {
			if(path.contains("?")) {
				queryString = path.substring(path.indexOf('?') + 1);
				path = path.substring(0, path.indexOf('?'));
			}
		}else if(method.equals("POST")) {
			queryString = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
		}
		
		if(queryString != null)
			params = HttpRequestUtils.parseQueryString(queryString);
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getVersion() {
		return version;
	}

	public String getHeader(String headerName) {
		return header.get(headerName);
	}

	public String getParameter(String paramName) {
		return params.get(paramName);
	}
}
