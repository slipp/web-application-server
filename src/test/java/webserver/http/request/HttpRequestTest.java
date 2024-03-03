package webserver.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import webserver.http.HttpMethod;

public class HttpRequestTest {

    @Test
    public void parse() throws IOException {
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
        assertThat(httpRequest.getPath()).isEqualTo(requestPath);
        assertThat(httpRequest.getHeader("Host").orElse("")).isEqualTo(host);
        assertThat(httpRequest.getHeader("Connection").orElse("")).isEqualTo("keep-alive");
        assertThat(httpRequest.getHeader("Accept").orElse("")).isEqualTo("*/*");
    }
}
