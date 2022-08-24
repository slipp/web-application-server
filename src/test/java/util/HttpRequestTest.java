package util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import webserver.HttpMethod;
import webserver.HttpRequest;

public class HttpRequestTest {
	private String testDirectory = "./src/test/resources/";
	
	
	@Test
	public void request_GET() throws Exception{
		InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals(HttpMethod.GET, request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi",request.getParameter("userId"));
		
	}
	@Test
	public void request_POST() throws Exception{
		InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals(HttpMethod.POST, request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive",request.getHeader("Connection"));
		assertEquals("javajigi",request.getParameter("userId"));
	}
}
