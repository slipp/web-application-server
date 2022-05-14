package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import model.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            // inputStream BufferedReader 생성
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            log.debug("request line : {}", line);

            if (line == null) {
                return;
            }

            String url = HttpRequestUtils.getUrl(line);
            Map<String, String> headers = new HashMap<String, String>();
            String contentType = null;

            // Request Header 내용을 Map<String, String> header 변수에 삽입 처리
            while (!line.equals("")) {
                log.debug("header : {}", line);
                line = br.readLine();
                String[] headerTokens = line.split(": ");
                if (headerTokens.length == 2) {
                    headers.put(headerTokens[0], headerTokens[1]);
                }
            }

            // POST 방식 처리
            if (url.startsWith("/create")) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                log.debug("body : {}", requestBody);

                Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);

                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug(user.toString());

                Database.addUser(user);

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "/index.html");

            } else if(url.startsWith("/login")){
                // Client Request Value
                String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                log.debug("body : {}", requestBody);

                // Parsing to Map value
                Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
                log.debug("userId : {}, password : {}",params.get("userId"), params.get("password"));

                // get User from Database
                User user = Database.getUser(params.get("userId"));

                // validation between User(database) and Client input Value
                // result handling
                if(user == null){
                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, "/index.html");
                    log.debug("User Not Found");
                } else if(user.getPassword().equals(params.get("password"))){
                    DataOutputStream dos = new DataOutputStream(out);
                    response302HeaderWithCookie(dos, "/index.html","logined=true");
                    log.debug("login success!!");
                } else{
                    DataOutputStream dos = new DataOutputStream(out);
                    response302HeaderWithCookie(dos, "/user/login_failed.html", "logined=failed");
                    log.debug("password Mismatch");
                }
            } else if(url.endsWith(".css")) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
            } else {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String url,String cookie){
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
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

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
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

    private int getContentLength(String line) {
        String[] headerTokens = line.split(" ");
        return Integer.parseInt(headerTokens[1].trim());
    }
}
