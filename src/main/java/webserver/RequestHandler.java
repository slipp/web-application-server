package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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

    @Override // 명시적으로 작성해주면 좋음.
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            log.debug("request line : {}", line);

            if (line == null){
                return;
            }

            // 첫 Line
            // ex) GET /index.html HTTP/1.1
            String[] tokens = line.split(" ");
            String parsed = tokens[1];
            boolean logined = false;

            // 기본 "/"일 땐 index.html로 변환
            if(parsed.equals("/"))
                parsed = "/index.html";

            int contentLength = 0;
            while(!line.equals("")){
                log.debug("header : {}", line);
                line = br.readLine();
                if (line.contains("Content-Length")){
                    contentLength = getContentLength(line);
//                    log.debug("contentLength: {}", contentLength);
                }
                if (line.contains("Cookie")){
                    logined = isLogin(line);
//                    log.debug("Cookies Contain, logined: {}", logined);
                }
            }

            // /user/create?userId=abc&password=123&name=%E3%85%8A%E3%85%88%E3%85%85&email=ad%40fdf.com 일 때,
            if(parsed.startsWith("/user/create")){
                log.debug("User create");
                String requestBody = IOUtils.readData(br, contentLength);
                log.debug("Request Body: {}", requestBody);
//                int index = parsed.indexOf("?");
//                String queryString = parsed.substring(index+1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                DataBase.addUser(user);
//                log.debug("User: {}", user);

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos);
            }
            else if(parsed.equals("/user/login")){
                String requestBody = IOUtils.readData(br, contentLength);
                log.debug("Request Body: {}", requestBody);
                Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
                User user = DataBase.findUserById(params.get("userId"));
                if(user == null){
                    responseResource(out, "/user/login_failed.html");
                    return;
                }

                if(user.getPassword().equals(params.get("password"))){
                    DataOutputStream dos = new DataOutputStream(out);
                    response302LoginSuccessHeader(dos);
                } else{
                    responseResource(out, "/user/login_failed.html");
                }
            }
            else if(parsed.startsWith("/user/list")){
//                log.debug("User List");
//                log.debug("Login State: {}", logined);
                if(!logined){
                    responseResource(out, "/user/login.html");
                    return;
                }
                Collection<User> users = DataBase.findAll();
                log.debug("All User: {}", users );
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for(User user : users){
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("<tr>");
                }
                sb.append("</table>");
                byte[] body = sb.toString().getBytes();
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            else if (parsed.endsWith(".css")){
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + parsed).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            }
            else{
                responseResource(out, parsed);

            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int length) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css \r\n");
            dos.writeBytes("Content-Length: " + length + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    private boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        log.debug("cookies: {}, value: {}", cookies, value);
        if(value == null)
            return false;
        return Boolean.parseBoolean(value);
    }

    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try{
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "; Path= /\r\n"); // Path를 임의로 지정해주어 모든 URL경로에서 쿠키에 접근 가능하도록 함.
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");

        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException{
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
