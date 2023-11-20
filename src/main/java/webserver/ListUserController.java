package webserver;

import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

public class ListUserController extends AbstractController{
	@Override
	public void	doGet(HttpRequest request, HttpResponse response)
	{
		if (!isLogin(request.getHeader("Cookie"))) {
			response.sendRedirect("/user/login.html");
		}
		Collection<User> users = DataBase.findAll();
		StringBuilder	sb = new StringBuilder();
		sb.append("<tabble border='1'>");
		for (User user : users) {
			sb.append("<tr>");
			sb.append("<td>" + user.getUserId() + "</td>");
			sb.append("<td>" + user.getName() + "</td>");
			sb.append("<td>" + user.getEmail() + "</td>");
			sb.append("</tr>");
		}
		response.forwardBody(sb.toString());
	}
	@Override
	public void	doPost(HttpRequest request, HttpResponse response){}
	public boolean isLogin(String line)
	{
		String[]	headerTokens = line.split(":");
		Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
		String value = cookies.get("logined");
		if (value == null)
			return false;
		return Boolean.parseBoolean(value);
	}
}