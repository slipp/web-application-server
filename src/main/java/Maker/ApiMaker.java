package Maker;

import api.BlankApi;
import api.GetCSS;
import api.GetPageApi;
import api.GetUserList;
import api.PostUserCreate;
import api.PostUserLogin;
import model.ApiInterface;
import webserver.Request;

/**
 * Created by woowahan on 2017. 4. 13..
 */
public class ApiMaker {
  public static ApiInterface getApiClass(Request request){
    String requestURL = request.getUrl();
    String method = request.getMethod();
    if(requestURL.startsWith("/user/create") && "POST".equals(method)){
      return new PostUserCreate(request.getBody(), request.getCookie(), request.getUrl());
    }
    if(requestURL.startsWith("/user/login") && "POST".equals(method)){
      return new PostUserLogin(request.getBody(), request.getCookie(), request.getUrl());
    }
    if(requestURL.startsWith("/css/")){
      return new GetCSS(request.getCookie(), request.getUrl());
    }
    if(requestURL.equals("/user/list")){
      return new GetUserList(request.getCookie(), request.getUrl());
    }
    if("/".equals(requestURL)){
      return new BlankApi(request.getCookie(), request.getUrl());
    }
    return new GetPageApi(request.getCookie(), request.getUrl());
  }
}
