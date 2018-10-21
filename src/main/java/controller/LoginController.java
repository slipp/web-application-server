package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

public class LoginController extends AbstractController {
	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		User user = DataBase.findUserById(request.getParameter("userId"));
		String redirectURL = null;
		String logined = null;

		if (user != null && user.getPassword().equals(request.getParameter("password"))) {
			redirectURL = "/index.html";
			logined = "true";
		} else {
			redirectURL = "/user/login_failed.html";
			logined = "false";
		}
		response.addHeader("Set-Cookie", "logined=" + logined);
		response.sendRedirect(redirectURL);
	}
}
