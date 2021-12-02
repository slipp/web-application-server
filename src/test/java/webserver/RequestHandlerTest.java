package webserver;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class RequestHandlerTest {

    private static final String HOST = "http://localhost:8080";

    private static final Logger log = LoggerFactory.getLogger(RequestHandlerTest.class);

    @BeforeClass
    public static void setUp() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(HOST+"/user/create");
        String body = "userId=test&password=test&name=daun&email=daun@daun.com";
        httpPost.setHeader("Accept", "*/*");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Host", "localhost:8080");
        httpPost.setEntity(new StringEntity(body));
        HttpResponse response = client.execute(httpPost);
        log.debug("{}", response);
        client.close();
    }

    @Test
    public void login_success() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(HOST+"/user/login");
        String body = "userId=test&password=test";
        httpPost.setHeader("Accept", "*/*");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Host", "localhost:8080");
        httpPost.setEntity(new StringEntity(body));
        HttpResponse response = client.execute(httpPost);
        Header[] headers = response.getAllHeaders();
        log.debug("{}", response);
        for(Header header : headers) {
            log.debug("{}", header);
        }
        client.close();
    }

}