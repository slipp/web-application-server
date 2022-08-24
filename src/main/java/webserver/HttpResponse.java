package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private DataOutputStream dos = null;
	private Map<String, String> headers = new HashMap<String, String>(); //헤더
	
	public HttpResponse(OutputStream out) {
		dos = new DataOutputStream(out);
		
	}
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	//HTMl ,CSS , 자바스크립트 파일 읽어 응답으로 보내기. 
	public void forward(String url) {
		byte[] body = null;
		try {
			body = Files.readAllBytes(new File("./webapp" + url).toPath());
			if(url.endsWith(".css")) {
				headers.put("Content-Type", "text/css");
			}else if (url.endsWith(".js")) {
				headers.put("Contenxt-Type", "application/javascript");
			}else {
				headers.put("Content-Type", "text/html;charset=utf-8");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		response200Header(body.length);
		responseBody(body);
	}
	
	//body 읽어서 header에 저장하기.(Stringbuilder를 읽어오기 위해 만든 메소드) 
	public void forwardBody(String body) {
		byte[] contents = body.getBytes();
		headers.put("Content-Type", "text/html;charset=utf-8");
		headers.put("Content-Length", contents.length+"");
		response200Header(contents.length);
		responseBody(contents);
	}
	//로그인 성공한 경우 (302코드)
	public void sendRedirect (String redirectUrl) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			processHeaders();
			dos.writeBytes("Location: "+redirectUrl+"\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	//내용 읽어오기 성공한 경우
	private void response200Header(int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			processHeaders();
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	//stream 쓰고 닫아주기. 
	private void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	//header를 처리하는 메소드이다. 
	private void processHeaders() {
		try {
			Set<String> keys = headers.keySet(); //header의 키들만 다 데려오기 ex: Content-Type, Content-Length 등 
			for (String key : keys) {
				dos.writeBytes(key +":"+ headers.get(key)  + "\r\n");// Content-Type: text/html \r\n
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
}
