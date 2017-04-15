package api;

import db.DataBase;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import model.PostRedirectApiModel;
import model.User;

/**
 * Created by woowahan on 2017. 4. 12..
 */
public class PostUserLogin extends PostRedirectApiModel {

  private boolean isLogin;

  public PostUserLogin(Map<String, String> body,
      Map<String, String> cookie, String url) {
    super(body, cookie, url);
  }

  @Override
  protected boolean init() {
    this.isLogin = validateUserInfo(body);
    return this.isLogin;
  }

  @Override
  protected String getCookieString() {
    if(this.isLogin)
      return "logined=true";
    return "logined=false";
  }

  @Override
  protected String getRedirectURL() {
    if(this.isLogin)
      return "http://localhost:8080/index.html";
    return "http://localhost:8080/user/login_failed.html";
  }


  private boolean validateUserInfo(Map<String, String> body) {
    if(body==null)
      return false;
    String userId = body.get("userId");
    String password = body.get("password");
    User user = DataBase.findUserById(userId);
    if(user==null)
      return false;
    if(user.getUserId().equals(userId) && user.getPassword().equals(password)){
      return true;
    }
    return false;
  }

}
