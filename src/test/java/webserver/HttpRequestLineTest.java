package webserver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpRequestLineTest {
    
    @Test
    public void getPath() {
        assertEquals("./index.html", HttpRequestLine.getPath("GET ./index.html"));
    }
}
