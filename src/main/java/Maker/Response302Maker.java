package Maker;

import java.io.DataOutputStream;
import java.io.IOException;
import model.Response;

/**
 * Created by woowahan on 2017. 4. 13..
 */
public class Response302Maker extends Response {

  @Override
  public void makeResponse(DataOutputStream dos, String bodyType, String cookieString, byte[] body) throws IOException {

  }

  @Override
  public void makeResponse(DataOutputStream dos, String redirectURL, String cookieString) throws IOException {
    dos.writeBytes("HTTP/1.1 302 Found \r\n");
    dos.writeBytes("Location: " + redirectURL + "\r\n");
    if (hasCookie(cookieString)) {
      dos.writeBytes("Set-Cookie: " + cookieString + "\r\n");
    }
    dos.writeBytes("\r\n");
  }
}
