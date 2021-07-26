package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Collection;
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
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String url = br.readLine().split(" ")[1];

            byte[] body;
            Map<String, String> headerInfo = HttpRequestUtils.parseHeader(br);
            int contentLength = Integer.parseInt(headerInfo.getOrDefault("Content-Length", "0"));
            String params = util.IOUtils.readData(br, contentLength);
            Map<String, String> cookies = util.HttpRequestUtils.parseCookies(headerInfo.getOrDefault("Cookie", null));
            log.debug("cookie: " + cookies.get("logined"));

            if("/".equals(url)) {
                body = "Welcome to My Page :>".getBytes();
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            else if("/user/create".equals(url)) {
                User user = createUser(params);
                DataBase.addUser(user);
                response302Header(dos, "/index.html");
            }
            else if("/user/login".equals(url)) {
                Map<String, String> login = loginParams(params);
                String checkId = login.getOrDefault("userId", null);
                String checkPassword = login.getOrDefault("password", null);
                String cookie, redirectUrl;

                if(isAccessible(checkId, checkPassword)) {
                    cookie = "logined=true";
                    redirectUrl = "/index.html";
                }
                else {
                    cookie = "logined=false";
                    redirectUrl = "/user/login_failed.html";
                }

                response302HeaderWithCookie(dos, redirectUrl, cookie);
            }
            else {
                body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private User createUser(String params) {
        Map<String, String> param = HttpRequestUtils.parseQueryString(params);
        return new User(param.get("userId"), param.get("password"), param.get("name"), param.get("email"));
    }

    private Map<String, String> loginParams(String params) {
        return HttpRequestUtils.parseQueryString(params);
    }

    private boolean isAccessible(String id, String password) {
        if(id == null || password == null) return false;
        User user = DataBase.findUserById(id);
        if(user == null) return false;

        return user.getPassword().equals(password);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String url, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Set-Cookie: " + cookie + " \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
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
