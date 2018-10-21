package controller;

import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.HttpResponse;

public class ListUserController extends AbstractController {
	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		Map<String, String> cookies = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));

		if (isLogin(cookies.get("logined"))) {
			StringBuilder sb = new StringBuilder();
			Collection<User> users = DataBase.findAll();
			sb.append("<table border='1'>");
			for (User user : users) {
				sb.append("<tr>");
				sb.append("<td>" + user.getUserId() + "</td>");
				sb.append("<td>" + user.getName() + "</td>");
				sb.append("<td>" + user.getEmail() + "</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			response.forwardBody(sb.toString());
		} else {
			response.sendRedirect("/index.html");
		}
	}

	private boolean isLogin(String logined) {
		System.out.println(logined);
		return Boolean.parseBoolean(logined);
	}
}
