package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static void join(Map<String, String> data) {
        final User user = new User(data);
        DataBase.addUser(user);
        logger.debug(String.format("새로운 user 추가됨. >> %s", user.toString()));
    }
}
