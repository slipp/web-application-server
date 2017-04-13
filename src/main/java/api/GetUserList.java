package api;

import db.DataBase;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import model.GetApiModel;
import model.User;

/**
 * Created by woowahan on 2017. 4. 12..
 */
public class GetUserList extends GetApiModel {


  public GetUserList(Map<String, String> cookie, String url) {
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
    if(userListCookieChecker(this.cookie)) {
      StringBuilder sb = new StringBuilder();
      sb.append("<br>").append("<tr>");
      sb.append(
          "<th scope=\"row\">1</th> <td>javajigi</td> <td>자바지기</td> <td>javajigi@sample.net</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
      sb.append("</tr>").append("<tr>");
      sb.append(
          "<th scope=\"row\">2</th> <td>slipp</td> <td>슬립</td> <td>slipp@sample.net</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
      sb.append("</tr>");
      int i = 3;
      for (User user : DataBase.findAll()) {
        sb.append("<tr>");
        sb.append("<th scope=\"row\">").append(i++).append("</th> <td>").append(user.getUserId())
            .append("</td> <td>").append(user.getName()).append("</td> <td>")
            .append(user.getEmail())
            .append("</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
        sb.append("</tr>");
      }
      sb.append("</br>");
      return sb.toString().getBytes();
    }
    return Files.readAllBytes(new File("./webapp/user/login_failed.html").toPath());
  }

  private boolean userListCookieChecker (Map<String, String> cookie){
    if(cookie==null) {
      return false;
    }
    if(!cookie.containsKey("logined"))
      return false;
    if(cookie.get("logined").equals("true")) {
      return true;
    }
    return false;
  }
}
