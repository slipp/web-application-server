package domain.user.repository;

import com.google.common.collect.Maps;
import domain.user.model.User;
import java.util.Collection;
import java.util.Map;

public class UserRepository {
    private static final Map<String, User> users = Maps.newHashMap();

    public static void save(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }

    public static void deleteAll() {
        users.clear();
    }
}
