package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

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
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            String firstLine = br.readLine();
            log.debug("{}", firstLine);
            String method = HttpRequestUtils.getMethod(firstLine);
            String url = HttpRequestUtils.getUrl(firstLine);
            Map<String, String> headers = parseHeader(br);
            String body = "";
            if(method.equalsIgnoreCase("post")) {
                body = parseBody(br, headers);
                log.debug("body:{}", body);
            }
            handleRequestUrl(method, url, body, dos);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private String parseBody(BufferedReader br, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        return IOUtils.readData(br, contentLength);
    }
    private Map<String, String> parseHeader(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while((line = br.readLine()) != null && !line.isEmpty()) {
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
            log.debug("{}", line);
        }
        return headers;
    }
    private List<String> parsePath(String url) {
        String path = url;
        String query = "";
        int querySeparatorIdx = url.indexOf("?");
        if(querySeparatorIdx != -1) {
            path = url.substring(0, querySeparatorIdx);
            query = url.substring(querySeparatorIdx + 1);
        }
        return Arrays.asList(path, query);
    }
    private void handleRequestUrl(String method, String url, String body, DataOutputStream out) throws IOException {
        List<String> queryPaths = parsePath(url);
        String path = queryPaths.get(0);
        String query = queryPaths.get(1);
        if(url.equals("/")) {
            handleRootPage(out);
            return;
        }
        if(url.equalsIgnoreCase("/index.html")) {
            handleIndexPage(out);
            return;
        }
        if(url.equalsIgnoreCase("/user/form.html")) {
            handleRegisterPage(out);
            return;
        }
        if(method.equalsIgnoreCase("get") && path.equalsIgnoreCase("/user/create")) {
            handleRegisterGet(query, "/index.html", out);
            return;
        }
        if(method.equalsIgnoreCase("post") && url.equalsIgnoreCase("/user/create")) {
            handleRegisterPost(body, "/index.html", out);
            return;
        }
        if(url.equalsIgnoreCase("/user/login.html")) {
            handleLoginPage(out);
            return;
        }
        if(method.equalsIgnoreCase("post") && url.equalsIgnoreCase("/user/login")) {
            handleLogin(body, out);
        }
        if(url.equalsIgnoreCase("/user/login_failed.html")) {
            handleLoginFailed(out);
        }
    }
    private void handleLogin(String body, DataOutputStream out) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryString(body);
        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        for (User foundUser : DataBase.findAll()) {
            if(user.getUserId().equals(foundUser.getUserId()) && user.getPassword().equals(foundUser.getPassword())) {
                handleIndexPage("/index.html", out, true);
                return;
            }
        }
        response302Header(out,"/user/login_failed.html", 0, false);
    }
    private void handleLoginFailed(DataOutputStream out) throws IOException {
        byte[] body = parseResponseBody("/user/login_failed.html");
        response200Header(out, body.length);
        out.write(body);
        out.flush();
    }
    private void handleLoginPage(DataOutputStream out) throws IOException {
        byte[] body = parseResponseBody("/user/login.html");
        response200Header(out, body.length);
        out.write(body);
        out.flush();
    }
    private void handleRegisterGet(String queryParams, String path, DataOutputStream out) {
        Map<String, String> params = HttpRequestUtils.parseQueryString(queryParams);
        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        log.debug("user:{}", user);
        DataBase.addUser(user);
        response302Header(out, path, 0);
    }
    private void handleRegisterPost(String body, String path, DataOutputStream out) {
        Map<String, String> params = HttpRequestUtils.parseQueryString(body);
        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
        log.debug("user:{}", user);
        DataBase.addUser(user);
        response302Header(out, path, 0);
    }
    private void handleRootPage(DataOutputStream out) {
        byte[] body = "Hello World".getBytes();
        response200Header(out, body.length);
        responseBody(out, body);
    }
    private byte[] parseResponseBody(String url) throws IOException {
        return Files.readAllBytes(new File("./webapp" + url.toLowerCase()).toPath());
    }
    private void handleIndexPage(DataOutputStream out) throws IOException {
        byte[] body = parseResponseBody("/index.html");
        response200Header(out, body.length);
        out.write(body);
        out.flush();
    }
    private void handleIndexPage(String path, DataOutputStream out) throws IOException {
        response302Header(out, path, 0);
    }
    private void handleIndexPage(String path, DataOutputStream out, boolean isLogin) throws IOException {
        response302Header(out, path, 0, isLogin);
    }
    private void handleRegisterPage(DataOutputStream out) throws IOException {
        byte[] body = parseResponseBody("/user/form.html");
        response200Header(out, body.length);
        out.write(body);
        out.flush();
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
    private void response302Header(DataOutputStream dos, String path, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }
    private void response302Header(DataOutputStream dos, String path, int lengthOfBodyContent, boolean isLogin) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + isLogin + "; Path=/\r\n");
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
