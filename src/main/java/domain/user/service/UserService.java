package domain.user.service;

import domain.user.model.User;
import domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static User signup(String userId, String password, String name, String email) {
        User user = User.of(userId, password, name, email);
        UserRepository.save(user);
        log.debug("{} user signup complete", user);
        return user;
    }

    public static boolean login(String userId, String password) {
        User user = UserRepository.findById(userId);
        if (user == null) {
            log.debug("User not found. userId: {}", userId);
            return false;
        }
        if (!user.getPassword().equals(password)) {
            log.debug("{} password not match", user);
            return false;
        }
        log.debug("{} user login complete", user);
        return true;
    }
}
