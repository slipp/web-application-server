package util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;
/**
 * Created by stripes on 2017. 1. 19..
 */
public class RequestUnitTest {
    private static final Logger logger = LoggerFactory.getLogger(RequestUnitTest.class);

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
}
