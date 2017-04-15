package model;

import Maker.Response200Maker;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by woowahan on 2017. 4. 13..
 */
public abstract class GetApiModel implements ApiInterface {

  protected Map<String, String> cookie;
  protected String url;
  protected Response200Maker response200Maker;

  public GetApiModel(Map<String, String> cookie, String url){
    this.cookie = cookie;
    this.url = url;
  }

  public void sendResponse(DataOutputStream dos) throws IOException {
    init();
    this.response200Maker = new Response200Maker();
    byte[] body = getBody(url);
    response200Maker.makeResponse(dos, getBodyType(), getCookieString(), body);
    dos.flush();
  }

  protected abstract boolean init();
  protected abstract String getBodyType();
  protected abstract String getCookieString();
  protected abstract byte[] getBody(String value) throws IOException;
}
