package api;

import Maker.Response200Maker;
import java.io.IOException;
import java.util.Map;
import model.GetApiModel;

/**
 * Created by woowahan on 2017. 4. 12..
 */
public class BlankApi extends GetApiModel {


  public BlankApi(Map<String, String> cookie, String url) {
    super(cookie, url);
  }

  @Override
  protected boolean init() {
    return true;
  }

  @Override
  protected String getBodyType() {
    return "html";
  }

  @Override
  protected String getCookieString() {
    return null;
  }

  @Override
  protected byte[] getBody(String value) throws IOException {
    return "Hello World".getBytes();
  }
}
