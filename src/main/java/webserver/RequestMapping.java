package webserver;

import java.util.HashMap;
import java.util.Map;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

public class RequestMapping {
    private static Map<String, Controller> controllers = new HashMap<String, Controller>();

    static {
        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new ListUserController());
    }

    public static Controller getController(String requestUrl) {
        return controllers.get(requestUrl);
    }
}
