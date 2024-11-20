package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import model.User;
import org.junit.Test;

import util.HttpRequestUtils.Pair;
import db.DataBase;

public class HttpRequestUtilsTest {

    @Test
    public void headerRequest() throws IOException {
        String httpRequest = "GET /path/resource HTTP/1.1\n" +
                "Host: localhost\n" +
                "Accept: */*\n" +
                "\n";

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        String url = HttpRequestUtils.headerRequest(inputStream);

        assertEquals("/path/resource", url);
    }

    @Test
    public void parseUrl() {
        String url = "/?data=234";
        String params = HttpRequestUtils.parseUrl(url);
        assertEquals("data=234", params);
    }

    @Test
    public void signUpRequest() {
        String url = "/user/create?userId=user&password=pass&name=user&email=user%40naver.com";
        Map<String, String> map = HttpRequestUtils.parseQueryString(url);
        DataBase.addUser(new User(map.get("userId"), map.get("password"),
                map.get("name"), map.get("email")));

        assertEquals(1, DataBase.findAll().size());
    }

    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is("password2"));
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters.isEmpty(), is(true));
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined"), is("true"));
        assertThat(parameters.get("JSessionId"), is("1234"));
        assertThat(parameters.get("session"), is(nullValue()));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair, is(new Pair("userId", "javajigi")));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair, is(nullValue()));
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair, is(new Pair("Content-Length", "59")));
    }
}
