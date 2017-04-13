package api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import model.GetApiModel;

/**
 * Created by woowahan on 2017. 4. 12..
 */
public class GetPageApi extends GetApiModel {


  public GetPageApi(Map<String, String> cookie, String url) {
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
    return Files.readAllBytes(new File("./webapp/" + value).toPath());
  }
}
