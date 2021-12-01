package service;

import controller.dto.UserLoginRequestDto;
import db.DataBase;
import controller.dto.UserCreateRequestDto;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class UserServiceTest {

    private static UserService userService;

    /* Default Variables */
    private static String default_userId = "ggobuk";
    private static String default_password = "ggobukpwd";
    private static String default_name = "Juha";
    private static String default_email = "ggobuk@gmail.com";

    @BeforeClass
    public static void setUp() {
        userService = new UserService();
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

        //then

    }

    @Test
    public void login_success_로그인 () {
        //given

        //when
        User user = userService.login(new UserLoginRequestDto(default_userId, default_password));
        //then
        assertEquals(user.getUserId(), default_userId);
    }

    @Test(expected = RuntimeException.class)
    public void login_fail_존재하지_않는_아이디 () {
        //given
        String NOT_EXIST_USER_ID = "NOT_EXIST_USER_ID";
        //when
        User user = userService.login(new UserLoginRequestDto(NOT_EXIST_USER_ID, default_password));

        //then
    }

    @Test(expected = RuntimeException.class)
    public void login_fail_비밀번호_오류 () {
        //given
        String WRONG_PASSWORD = "WRONG_PASSWORD";
        //when
        User user = userService.login(new UserLoginRequestDto(default_userId, WRONG_PASSWORD));
        //then
    }


}