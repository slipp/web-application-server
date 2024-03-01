package webserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class StaticFileControllerTest {

    @Test
    public void acceptHtml() throws IOException {
        // given
        String method = "GET";
        String requestPath = "/user/form.html";
        String queryString = "";
        String uri = requestPath + queryString;
        String host = "localhost:8080";
        String request = String.format("""
                %s %s HTTP/1.1
                Host: %s
                Connection: keep-alive
                Accept: text/html,*/*;q=0.1
                        
                """,
            method, uri, host);
        HttpRequest httpRequest = HttpRequest.from(new ByteArrayInputStream(request.getBytes()));

        // when
        HttpResponse response = StaticFileController.controll(httpRequest);

        // then
        assertThat(response.getHeaders().get("Content-Type")).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    public void acceptCss() throws IOException {
        // given
        String method = "GET";
        String requestPath = "/css/styles.css";
        String queryString = "";
        String uri = requestPath + queryString;
        String host = "localhost:8080";
        String request = String.format("""
                %s %s HTTP/1.1
                Host: %s
                Connection: keep-alive
                Accept: text/css,*/*;q=0.1
                        
                """,
            method, uri, host);
        HttpRequest httpRequest = HttpRequest.from(new ByteArrayInputStream(request.getBytes()));

        // when
        HttpResponse response = StaticFileController.controll(httpRequest);

        // then
        assertThat(response.getHeaders().get("Content-Type")).isEqualTo("text/css;charset=utf-8");
    }
}