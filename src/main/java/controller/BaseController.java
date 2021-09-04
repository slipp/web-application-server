package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import java.util.Map;

public class BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    public static void join(String queryString) {
        final Map<String, String> request = HttpRequestUtils.parseQueryString(queryString);
        final User user = new User(request);
        DataBase.addUser(user);
        logger.debug(String.format("새로운 user 추가됨. >> %s", user.toString()));
    }
}
