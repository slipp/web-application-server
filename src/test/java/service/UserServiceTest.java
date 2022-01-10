package service;

import controller.dto.UserLoginRequestDto;
import db.DataBase;
import controller.dto.UserCreateRequestDto;
import model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class UserServiceTest {

    private static UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);
    /* Default Variables */
    private static String first_userId = "test1";
    private static String first_password = "test1";
    private static String first_name = "test1";
    private static String first_email = "test1@gmail.com";
    private static String second_userId = "test2";
    private static String second_password = "test2";
    private static String second_name = "test2";
    private static String second_email = "test2@gmail.com";


    @BeforeClass
    public static void setUp() {
        userService = new UserService();
        DataBase.addUser(new User(first_userId, first_password, first_name, first_email));
        DataBase.addUser(new User(second_userId, second_password, second_name, second_email));
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
        String userId = "test1";
        String password = "test1";
        String name = "test1";
        String email = "test1@gmail.com";
        UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto(userId, password, name, email);

        //when
        userService.create(userCreateRequestDto);

        //then

    }

    @Test
    public void login_success_로그인 () {
        //given

        //when
        User user = userService.login(new UserLoginRequestDto(first_userId, first_password));
        //then
        assertEquals(user.getUserId(), first_userId);
    }

    @Test(expected = RuntimeException.class)
    public void login_fail_존재하지_않는_아이디 () {
        //given
        String NOT_EXIST_USER_ID = "NOT_EXIST_USER_ID";
        //when
        User user = userService.login(new UserLoginRequestDto(NOT_EXIST_USER_ID, first_password));

        //then
    }

    @Test(expected = RuntimeException.class)
    public void login_fail_비밀번호_오류 () {
        //given
        String WRONG_PASSWORD = "WRONG_PASSWORD";
        //when
        User user = userService.login(new UserLoginRequestDto(first_userId, WRONG_PASSWORD));
        //then
    }

    @Test
    public void findAllUser() {
        List<User> userList = userService.findAllUser();
        userList.forEach(user -> log.debug("{}", user));
        assertEquals(3,userList.size());
    }
}