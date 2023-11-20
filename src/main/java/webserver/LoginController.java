package webserver;

import db.DataBase;
import model.User;

public class LoginController extends AbstractController
{
	@Override
	public void doPost(HttpRequest request, HttpResponse response)
	{
		User user = DataBase.findUserById(request.getParameter("userId"));
		if (user != null) {
			if (user.login(request.getParameter("password"))) {
				response.addHeader("Set-Cookie", "logined=true");
				response.sendRedirect("/index.html");
			} else {
				response.sendRedirect("/user/login_failed.html");
			}
		}else {
			response.sendRedirect("/user/login_failed.html");
		}
	}
	@Override
	public void doGet(HttpRequest request, HttpResponse response) {}
}