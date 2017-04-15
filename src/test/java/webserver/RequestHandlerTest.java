package webserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.Buffer;
import model.User;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by woowahan on 2017. 4. 9..
 */
public class RequestHandlerTest {


  @Test
  public void getContentsBody() throws IOException {
    String contents = "POST /user/create HTTP/1.1\n"
        + "Host: localhost:8080\n"
        + "Connection: keep-alive\n"
        + "Content-Length: 59\n"
        + "Content-Type: application/x-www-form-urlencoded\n"
        + "Accept: */*\n"
        + "\n"
        + "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net\n";
    InputStream in = new ByteArrayInputStream(contents.getBytes("UTF-8"));
    Request request = Request.parse(in);
    System.out.println(request.getBody());
    Assert.assertNotNull(request.getBody());
  }
}
