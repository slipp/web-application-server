package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            HttpRequest httpRequest = new HttpRequest(br);
            
            String http_method = httpRequest.getHttpMethod();
            String target_url = httpRequest.getPath();
            
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = new byte[0];

            if(target_url.equals("/user/create")) {
                addUserToDataBase(newUserFromParams(getPostData(br, Integer.parseInt(httpRequest.getHeader("Content-Length")))));

                response302Header(dos, "../../index.html");
            } else if (http_method.equals("POST") && target_url.equals("/user/login")) {
                Map<String, String> postData = getPostData(br, Integer.parseInt(httpRequest.getHeader("Content-Length")));
                if(isCorrectPassword(postData, findUserFromDataBase(postData.get("userId")))) {
                    response302HeaderForCookie(dos, "../../index.html","logined=true");
                } else {
                    response302HeaderForCookie(dos, "./login_failed.html","logined=false");
                }
                
            } else if(target_url.equals("/user/list")) {
                if(isLogined(httpRequest.getHeader("Cookie"))) {
                    response302Header(dos, "./list.html");
                } else {
                    response302Header(dos, "./login.html");
                }
            } else {
                body = Files.readAllBytes(new File("./webapp" + target_url).toPath());

                if(target_url.contains(".css")) {
                    response200HeaderForCss(dos, body.length);
                } else {
                    response200Header(dos, body.length);
                }
            }

            responseBody(dos, body);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean isCorrectPassword(Map<String, String> params, User user) {
        return user.getPassword().equals(params.get("password"));
    }

    private boolean isLogined(String cookie) {
        return cookie.contains("logined=true");
    }

    private void addUserToDataBase(User user) {
        DataBase.addUser(user);
    }

    private User findUserFromDataBase(String userId) {
        return DataBase.findUserById(userId);
    }

    private User newUserFromParams(Map<String, String> params) {
        return User.build(
                params.get("userId"),
                params.get("password"),
                params.get("name"),
                params.get("email")
        );
    }

    private Map<String, String> getPostData(BufferedReader br, int contentLength) throws IOException {
        return HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes(String.format("Location: %s\r\n", redirectUrl));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderForCookie(DataOutputStream dos, String redirectUrl, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes(String.format("Location: %s\r\n", redirectUrl));
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderForCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
