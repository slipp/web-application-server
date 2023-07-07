package webserver;


import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class RequestHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandlerTest.class);
    @Test
    public void checkParam() {
        String queryString = "userId=test01&password=test01&name=&email=";
        String[] param = queryString.split("&");

        Map<String, Object> map = new HashMap<>();
        for (String s : param) {
            String[] tempList = s.split("=");
            if (tempList.length > 1) {
                map.put(tempList[0], tempList[1]);
            }else{
                map.put(tempList[0], "");
            }
        }
        String userId = (String) map.get("userId");
        String password = (String) map.get("password");
        String name = (String) map.get("name");
        String email = (String) map.get("email");

        User user = new User(userId, password, name, email);
//        assertThat(user).isEqualTo(new User("test01", "test01", "", ""));
    }
}
