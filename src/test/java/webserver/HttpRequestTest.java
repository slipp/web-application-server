package webserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @Test
    public void from() throws IOException {
        String method = "GET";
        String uri = "/index.html";
        String host = "localhost:8080";

        String request = String.format("%s %s HTTP/1.1\n" +
            "Host: %s\n" + 
            "Connection: keep-alive\n" + 
            "Accept: */*\n", 
            method, uri, host);

        InputStream in = new ByteArrayInputStream(request.getBytes());
        HttpRequest httpRequest = HttpRequest.from(in);

        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.valueOf(method));
        assertThat(httpRequest.getUri()).isEqualTo(uri);
    }
}
