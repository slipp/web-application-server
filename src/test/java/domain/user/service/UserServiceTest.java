package domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.user.model.User;
import domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @BeforeEach
    void setUp() {
        UserRepository.deleteAll();
    }

    @Test
    void signup() {
        // given
        String userId = "test";
        String password = "password";
        String name = "name";
        String email = "email";

        // when
        UserService.signup(userId, password, name, email);
        User signupUser = UserRepository.findById(userId);

        // then
        assertEquals(userId, signupUser.getUserId());
        assertEquals(password, signupUser.getPassword());
        assertEquals(name, signupUser.getName());
        assertEquals(email, signupUser.getEmail());
    }

    @Test
    void login() {
        // given
        String userId = "test";
        String password = "password";
        String name = "name";
        String email = "email";
        UserService.signup(userId, password, name, email);

        // when
        assertTrue(UserService.login(userId, password));
    }
}