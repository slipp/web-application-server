package model;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public User(final Map<String, String> request) {
        final Set<Map.Entry<String, String>> entries = request.entrySet();
        final Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            switch (entry.getKey()) {
                case "userId":
                    this.userId = entry.getValue();
                    break;
                case "password":
                    this.password = entry.getValue();
                    break;
                case "name":
                    this.name = entry.getValue();
                    break;
                case "email":
                    this.email = entry.getValue();
                    break;
                default:
                    break;
            }
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
