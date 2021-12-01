package controller.dto;

import model.User;

public class UserLoginRequestDto {

    private String userId;
    private String password;

    public String getUserId() {
        return userId;
    }
    public String getPassword() {
        return password;
    }

    public UserLoginRequestDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginRequestDto{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
