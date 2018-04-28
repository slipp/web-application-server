package webserver;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RequestLineTest {

    @Test
    public void getRequestResource() {
        RequestLine sut = RequestLine.of("GET /index.html?a=1&b=2 HTTP/1.1");

        String resource = sut.getRequestResource();

        assertThat(resource, is("/index.html"));
    }

    @Test
    public void getRequestParams() {
        RequestLine sut = RequestLine.of("GET /index.html?a=1&b=2 HTTP/1.1");

        String params = sut.getRequestParam();

        assertThat(params, is("a=1&b=2"));
    }

    @Test
    public void getRequestParamsWhenParamIsAbsent() {
        RequestLine sut = RequestLine.of("GET /index.html HTTP/1.1");

        String params = sut.getRequestParam();

        assertThat(params, is(""));
    }

}