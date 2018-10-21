package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

public class CreateUserController extends AbstractController {
	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		User user = new User(request.getParameter("userId"), request.getParameter("password"),
				request.getParameter("name"), request.getParameter("email"));
		
		DataBase.addUser(user);
		
		response.sendRedirect("/index.html");
	}
}
