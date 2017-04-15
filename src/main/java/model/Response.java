package model;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by woowahan on 2017. 4. 13..
 */
public abstract class Response {
  public abstract void makeResponse(DataOutputStream dos, String bodyType, String cookieString, byte[] body) throws IOException;
  public abstract void makeResponse(DataOutputStream dos, String redirectURL, String cookieString) throws IOException;
  protected static boolean hasCookie(String cookieString){
    if (cookieString==null || cookieString.equals("")){
      return false;
    }
    return true;
  }
}
