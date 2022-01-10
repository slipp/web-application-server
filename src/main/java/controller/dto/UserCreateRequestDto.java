package controller.dto;

import model.User;

public class UserCreateRequestDto {

    private String userId;
    private String password;
    private String name;
    private String email;

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

    public UserCreateRequestDto(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public User toEntity() {
        return new User(this.userId, this.password, this.name, this.email);
    }

    @Override
    public String toString() {
        return "UserCreateRequestDto{" +
                "userId='" + userId + '\'' +
                ", password='" + "[PROTECTED]" + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
