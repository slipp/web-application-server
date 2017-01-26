package util;

import db.DataBase;
import model.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;
/**
 * Created by stripes on 2017. 1. 19..
 */
public class RequestHandlerUnitTest {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerUnitTest.class);

    @Test
    public void readUrl() throws Exception {
        // token 분리에 관한 단위 테스트
        String str = "GET /index.html HTTP/1.1";
        String[] tokens = str.split(" ");
        String url = null;

        assertEquals("/index.html", tokens[1]);

        str = "GET /user/login.html HTTP/1.1";
        tokens = str.split(" ");
        assertEquals("/user/login.html", tokens[1]);
    }

//    @Test
//    public void registerForm() throws Exception {
//        // 회원 가입을 위한 단위 테스트
//        String getUrl = "GET /user/create?userId=hello&password=1234&name=bbigbros&email=a%40gmail.com HTTP/1.1";
//        String[] tokens = getUrl.split(" ");
//        Map<String, String> testResult = null;
//
//        for(String s : tokens) {
//            logger.debug("test: {}", s);
//        }
//
//        Map<String, String> m = HttpRequestUtils.parseQueryString(tokens[1]);
//        logger.debug("{}", m);
//        Iterator<String> keys = m.keySet().iterator();
//
//        while(keys.hasNext()){
//            String key = keys.next();
//            testResult.put(key, m.get(key));
//            logger.debug("key: {}, value: {}", key, m.get(key));
//        }
//    }
    @Test
    public void loginTest() throws Exception {
        User u1 = new User("hello", "1234", "bbigbros", "a@a.com");
        User u2 = new User("wow", "1234", "banana", "b@b.com");
        DataBase.addUser(u1);
        DataBase.addUser(u2);
        User user= DataBase.findUserById("hello");
        Collection<User> allUser = DataBase.findAll();
        logger.debug("debug: {}",user.toString());
        logger.debug("all user : {}", allUser);
    }
}
