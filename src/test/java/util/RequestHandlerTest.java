package util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class RequestHandlerTest {

	@Test
	public void readRequestHeaderTest() {
		String URI = "GET /index.html HTTP/1.1";
		String[] requestHeader = URI.split(" ");
		assertEquals("/index.html", requestHeader[1]);
	}
	
	@Test
	public void readFileTest() throws IOException {
		byte[] file = Files.readAllBytes(Paths.get("webapp/index.html"));
		assertNotNull(file);
		System.out.println(new String(file));
	}

}
