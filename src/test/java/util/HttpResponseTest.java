package util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import webserver.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class HttpResponseTest {
	private String testDirectory = "./src/test/resources/";
	
	@Test
	public void responseForward() throws Exception{
		//Http_Forward.txt 결과는 응답 body에 index.html에 포함되어 있어야 한다. 이걸 어떻게 읽지..?
		HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));
		response.forward("/index.html");
	}
	
	@Test
	public void responseRedirect() throws Exception{
		//Http_Redirect.txt 결과는 응답 headere에 
		//Location 정보가 /index.thml 로 포함되어 있어야 한다.
		HttpResponse response = new HttpResponse(createOutputStream("Http_Redirect.txt"));
		response.sendRedirect("/index.html");

	}
	
	@Test
	public void responseCookies() throws Exception{
		//Http_Cookie.txt 결과는 응답 header 에 Set-Cookie 값으로
		//logined-true 값이 포함되어 있어야 한다.
		HttpResponse response = new HttpResponse(createOutputStream("Http_Cookie.txt"));
		response.addHeader("Set-Cookie:", "logined=true");
		response.sendRedirect("/index.html");
		
	}
	private OutputStream createOutputStream(String filename) throws FileNotFoundException{
		return new FileOutputStream(new File(testDirectory + filename));
	}
}
