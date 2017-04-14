package http;

import static org.junit.Assert.*;

import org.junit.Test;

public class HttpHeadersTest {
	@Test
	public void add() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Connection: keep-alive");
		assertEquals("keep-alive", headers.getHeader("Connection"));
	}
}
