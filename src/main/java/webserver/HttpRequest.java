package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	//HTTP 메소드 , URL, 헤더, 본문을 분리하는 작업을 한다. 
	private RequestLine requestline;
	private Map<String, String> headers = new HashMap<String, String>(); //헤더
	private Map<String, String> params = new HashMap<String, String>(); //post로 들어오는 파리미터 받아오기 
	private HttpMethod method;
	
	
	public HttpRequest(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			
			if (line == null) {
				return;
			}
			//1.request line 받아오기 
			requestline = new RequestLine(line);
			
			
			//2.header 받아오기 
			line = br.readLine();
			while (!line.equals("")) {
				log.debug("header: {}", line); // header : Connection: keep-alive
				String[] tokens = line.split(":"); // tokens = [Connection, keep-alive]
				//2.1.해더는 Map<String,String>에 저장한다. 
				headers.put(tokens[0].trim(), tokens[1].trim()); 
				line = br.readLine(); 
				
			}
			if(method.POST.equals(getMethod())) {
				String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
				params = HttpRequestUtils.parseQueryString(body);
				
			}else {
				//get 메소드인 경우 url에서 추출해왔으므로 그대로 담아주기 
				params = requestline.getParams();
			}
		} catch (IOException io) {
			log.error(io.getMessage());
		}

	}
	
	public String getHeader(String name) {
		return headers.get(name);
	}
	public HttpMethod getMethod() {
		return requestline.getMethod();
	}
	public String getPath() {
		return requestline.getPath();
	}
	public String getParameter(String name) {
		return params.get(name);
	}
}
