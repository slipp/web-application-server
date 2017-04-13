package model;

import Maker.Response302Maker;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by woowahan on 2017. 4. 13..
 */
public abstract class PostRedirectApiModel implements ApiInterface {

  protected Map<String, String> body;
  protected Map<String, String> cookie;
  protected String url;
  protected Response302Maker response302Maker;

  public PostRedirectApiModel(Map<String, String> body, Map<String, String> cookie, String url){
    this.body = body;
    this.cookie = cookie;
    this.url = url;
  }

  public void sendResponse(DataOutputStream dos) throws IOException {
    init();
    this.response302Maker = new Response302Maker();
    response302Maker.makeResponse(dos, getRedirectURL(), getCookieString());
    dos.flush();
  }

  protected abstract boolean init();
  protected abstract String getCookieString();
  protected abstract String getRedirectURL();
}
