package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String HTML_FILE_PATH = "." + File.separator + "webapp";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            String[] requestLine = line.split(" ");

            // request header
            Map<String, String> requestHeaders = new HashMap<>();
            while ((line = br.readLine()) != null && !line.equals("")) {
                log.debug("[Request Header] {}", line);
                String[] headerToken = line.split(": ");
                requestHeaders.put(headerToken[0], headerToken[1]);
            }

            String httpMethod = requestLine[0];
            String requestUrl = requestLine[1];
            if (httpMethod.equals("POST") && requestUrl.startsWith("/user/create")) {
                // request body
                String requestBody = IOUtils.readData(br, Integer.parseInt(requestHeaders.get("Content-Length")));
                log.debug("[Request Body] {}", requestBody);

                Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(requestBody);
                String userId = queryStringMap.get("userId");
                String password = queryStringMap.get("password");
                String name = queryStringMap.get("name");
                String email = queryStringMap.get("email");
                User user = new User(userId, password, name, email);
                log.debug("user create {}", user);
                DataBase.addUser(user);
                redirect(dos, "/index.html");
            } else if (httpMethod.equals("POST") && requestUrl.equals("/user/login")) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(requestHeaders.get("Content-Length")));
                log.debug("[Request Body] {}", requestBody);
                Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(requestBody);
                String userId = queryStringMap.get("userId");
                String password = queryStringMap.get("password");
                User user = DataBase.findUserById(userId);

                Map<String, String> responseHeader = new HashMap<>();
                if (user == null || !user.getPassword().equals(password)) {
                    // 로그인 실패
                    responseHeader.put("Set-Cookie", "logined=false; Path=/");
                    redirect(dos, "/user/login_failed.html", responseHeader);
                    return;
                }
                // 로그인 성공
                responseHeader.put("Set-Cookie", "logined=true; Path=/");
                redirect(dos, "/index.html", responseHeader);
            } else if (httpMethod.equals("GET") && requestUrl.equals("/user/list")) {
                if (!isLogined(requestHeaders)) {
                    redirect(dos, "/index.html");
                    return;
                }
                List<User> users = new ArrayList<>(DataBase.findAll());
                byte[] body = responseUsers(users);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else {
                byte[] body = Files.readAllBytes(new File(HTML_FILE_PATH + requestLine[1]).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] responseUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>userId</th>");
        sb.append("<th>name</th>");
        sb.append("<th>email</th>");
        sb.append("</tr>");

        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(user.getUserId());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(user.getName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(user.getEmail());
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString().getBytes();
    }

    private boolean isLogined(Map<String, String> requestHeaders) {
        String cookies = requestHeaders.get("Cookie");
        Map<String, String> cookieMap = HttpRequestUtils.parseCookies(cookies);
        String logined = cookieMap.get("logined");
        if (logined == null) {
            return false;
        }

        return Boolean.parseBoolean(logined);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void redirect(DataOutputStream dos, String redirectUrl) {
        redirect(dos, redirectUrl, null);
    }

    private void redirect(DataOutputStream dos, String redirectUrl, Map<String, String> headers) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            if (headers != null && !headers.isEmpty()) {
                for (String key : headers.keySet()) {
                    dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
                }
            }
            dos.flush();
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
