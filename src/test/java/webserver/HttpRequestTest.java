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
        String requestPath = "/request-path";
        String queryString = "?key=value";
        String uri = requestPath + queryString;
        String host = "localhost:8080";

        String request = String.format("""
                %s %s HTTP/1.1
                Host: %s
                Connection: keep-alive
                Accept: */*
                        
                """,
            method, uri, host);

        InputStream in = new ByteArrayInputStream(request.getBytes());
        HttpRequest httpRequest = HttpRequest.from(in);

        assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.valueOf(method));
        assertThat(httpRequest.getRequestPath()).isEqualTo(requestPath);
    }
}
