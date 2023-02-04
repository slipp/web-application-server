package util;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HttpRequestTest {

    @Test
    public void extractHttpRequest1 () throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        String path = System.getProperty("user.dir");
        System.out.println("Working Directory = " + path);

        File file = new File("src/test/resources/Http_GET.txt");
        InputStream in = new FileInputStream(file);
        InputStreamReader inStr = new InputStreamReader(in);
        httpRequest.extractHttpRequest(inStr);

        assertThat(httpRequest.method, is("GET"));
        assertThat(httpRequest.url, is("/usr/create"));
        assertThat(httpRequest.header.get("Connecttion"), is("keep-alive"));
        assertThat(httpRequest.queryString.get("userID"), is("javajigi"));
    }

    @Test
    public void extractHttpRequest2 () throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        String path = System.getProperty("user.dir");
        System.out.println("Working Directory = " + path);

        File file = new File("src/test/resources/Http_GET2.txt");
        InputStream in = new FileInputStream(file);
        InputStreamReader inStr = new InputStreamReader(in);
        httpRequest.extractHttpRequest(inStr);

        assertThat(httpRequest.method, is("GET"));
        assertThat(httpRequest.url, is("/usr/create"));
        assertThat(httpRequest.header.get("Connecttion"), is("keep-alive"));
    }

    @Test
    public void extractHttpRequest3 () throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        String path = System.getProperty("user.dir");
        System.out.println("Working Directory = " + path);

        File file = new File("src/test/resources/Http_POST.txt");
        InputStream in = new FileInputStream(file);
        InputStreamReader inStr = new InputStreamReader(in);
        httpRequest.extractHttpRequest(inStr);

        assertThat(httpRequest.method, is("POST"));
        assertThat(httpRequest.url, is("/usr/create"));
        assertThat(httpRequest.header.get("Connecttion"), is("keep-alive"));
        assertThat(httpRequest.queryString.get("userID"), is("javajigi"));
    }

}
