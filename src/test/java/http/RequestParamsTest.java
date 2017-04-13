package http;

import static org.junit.Assert.*;

import org.junit.Test;

public class RequestParamsTest {
	@Test
	public void add() throws Exception {
		RequestParams params = new RequestParams();
		params.addQueryString("id=1");
		params.addBody("userId=javajigi&password=password");
		assertEquals("1", params.getParameter("id"));
		assertEquals("javajigi", params.getParameter("userId"));
		assertEquals("password", params.getParameter("password"));
	}
}
