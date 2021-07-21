package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.DataTruncation;

import db.DataBase;
import model.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }

    @Test
    public void findUserById() {
        User user1 = new User("id_1", "password_1", "name_1", "email_1");
        User user2 = new User("id_2", "password_2", "name_2", "email_2");
        DataBase.addUser(user1);
        DataBase.addUser(user2);

        assertThat(DataBase.findUserById("id_1"), is(user1));
    }
}
