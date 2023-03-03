package util;

import junit.framework.TestCase;
import org.junit.Test;

public class InputStreamParserTest extends TestCase {
    public void testName() {
        String input = "GET /index.html HTTP/1.1";
        String s = InputStreamParser.urlParse(input);
        System.out.println(s);
    }

    @Test
    public void 회원가입Test() {
        String input = "GET /user/create?userId=user&password=1234&name=&email= HTTP/1.1";
        String s = InputStreamParser.urlParse(input);
        System.out.println("result : " + s);
    }
}