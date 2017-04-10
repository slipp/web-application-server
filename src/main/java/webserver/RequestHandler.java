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

            Map<String, String> headers = getHeaders(br);
            
            String http_method = headers.get("Http_Method");
            String target_url = headers.get("Target_Url");
            
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = new byte[0];

            if(target_url.equals("/user/create")) {
                Map<String, String> params = HttpRequestUtils.parseQueryString(IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length"))));

                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );

                DataBase.addUser(user);
                
                response302Header(dos, "../../index.html");
            } else if (http_method.equals("POST") && target_url.equals("/user/login")) {
                Map<String, String> params = HttpRequestUtils.parseQueryString(IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length"))));

                User user = DataBase.findUserById(params.get("userId"));

                if(user.getPassword().equals(params.get("password"))) {
                    response302HeaderForCookie(dos, "../../index.html","logined=true");
                } else {
                    response302HeaderForCookie(dos, "./login_failed.html","logined=false");
                }
                
            } else if(target_url.equals("/user/list")) {
                if(headers.get("Cookie").contains("logined=true")) {
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
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private Map<String, String> getHeaders(BufferedReader br) throws IOException {
        String line = br.readLine();

        if(line == null) {
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Http_Method", line.split(" ")[0]);
        headers.put("Target_Url", line.split(" ")[1]);
        log.debug("Http_Method : {}", line.split(" ")[0]);
        log.debug("Target_Url : {}", line.split(" ")[1]);

        while (!"".equals(line)) {
            line = br.readLine();
            if(!"".equals(line)) {
                headers.put(HttpRequestUtils.parseHeader(line).getKey(),HttpRequestUtils.parseHeader(line).getValue());
                log.debug("{} : {}", HttpRequestUtils.parseHeader(line).getKey(), HttpRequestUtils.parseHeader(line).getValue());
            }
        }

        return headers;
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
