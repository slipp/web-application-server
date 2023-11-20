package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;

public class CreateUserController extends AbstractController{
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	@Override
	public	void doPost(HttpRequest request, HttpResponse response)
	{
		User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), 
				request.getParameter("email"));
		log.debug("user : {}", user);
		DataBase.addUser(user);
		response.sendRedirect("/index.html");
	}
	@Override
	public	void doGet(HttpRequest request, HttpResponse response) {}
}
