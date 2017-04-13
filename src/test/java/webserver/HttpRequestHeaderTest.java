package webserver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpRequestHeaderTest {
    
    @Test
    public void getHeader() {
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader();
        httpRequestHeader.add("Connection : keep-alive");
        assertEquals("keep-alive", httpRequestHeader.getHeader("Connection"));
    }
}
