package webserver;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RequestLineTest {

    @Test
    public void getRequestResource() throws Exception {
        RequestLine sut = RequestLine.of("GET /index.html HTTP/1.1");

        String result = sut.getRequestResource();

        assertThat(result, is("/index.html"));
    }

}