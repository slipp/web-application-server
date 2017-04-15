package api;

import db.DataBase;
import java.util.Map;
import model.PostRedirectApiModel;
import model.User;

/**
 * Created by woowahan on 2017. 4. 12..
 */
public class PostUserCreate extends PostRedirectApiModel {


  public PostUserCreate(Map<String, String> body, Map<String, String> cookie, String url) {
    super(body, cookie, url);
  }

  @Override
  protected boolean init() {
    return true;
  }

  @Override
  protected String getCookieString() {
    return null;
  }

  @Override
  protected String getRedirectURL() {
    boolean isMake = this.makeUser(this.body);
    if(isMake)
      return "http://localhost:8080/index.html";
    return "http://localhost:8080/";
  }


  private boolean makeUser(Map<String, String> body) {
    if(body==null) {
      return false;
    }
    User user = new User(body.get("userId"), body.get("password"), body.get("name"), body.get("email"));
    if(user.nullValueCheck())
      return false;
    DataBase.addUser(user);
    return true;
  }
}
