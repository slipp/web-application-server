package Maker;

import java.io.DataOutputStream;
import java.io.IOException;
import model.Response;

/**
 * Created by woowahan on 2017. 4. 13..
 */
public class Response200Maker extends Response {

  @Override
  public void makeResponse(DataOutputStream dos, String bodyType, String cookieString, byte[] body) throws IOException {
    dos.writeBytes("HTTP/1.1 200 OK \r\n");
    dos.writeBytes("Content-Type: text/"+ bodyType +";charset=utf-8\r\n");
    dos.writeBytes("Content-Length: " + body.length + "\r\n");
    if(hasCookie(cookieString))
      dos.writeBytes("Set-Cookie: " + cookieString + "\r\n");
    dos.writeBytes("\r\n");
    dos.write(body, 0, body.length);
  }

  @Override
  public void makeResponse(DataOutputStream dos, String redirectURL, String cookieString)
      throws IOException {

  }

}
