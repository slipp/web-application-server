package service;

import db.DataBase;
import controller.dto.UserCreateRequestDto;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class UserServiceTest {

    private static UserService userService;

    @BeforeClass
    public static void setUp() {
        userService = new UserService();

        String default_userId = "ggobuk";
        String default_password = "ggobukpwd";
        String default_name = "Juha";
        String default_email = "ggobuk@gmail.com";
        DataBase.addUser(new User(default_userId, default_password, default_name, default_email));

    }

    @Test
    public void create_success_회원가입 () {
         //given
        String userId = "daun";
        String password = "daunpwd";
        String name = "daun";
        String email = "daun@gmail.com";
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto(userId, password, name, email);

        //when
        userService.create(userCreateRequestDto);

        //then
        User user = DataBase.findUserById("daun");
        assertEquals(user.getUserId(), userId);
        assertEquals(user.getPassword(), password);
        assertEquals(user.getName(), name);
        assertEquals(user.getEmail(), email);
    }

    @Test(expected = RuntimeException.class)
    public void create_fail_중복_아이디 () {
        //given
        String userId = "ggobuk";
        String password = "daunpwd";
        String name = "daun";
        String email = "daun@gmail.com";
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto(userId, password, name, email);

        //when
        userService.create(userCreateRequestDto);
    }


}