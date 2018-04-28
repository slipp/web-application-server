package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static List<User> users = new ArrayList<>();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            String line = bufferedReader.readLine();

            if (line == null) {
                return;
            }

            RequestLine requestLine = RequestLine.of(line);

            while(!"".equals(line)) {
                line = bufferedReader.readLine();
                log.debug(line);
            }

            String resource = requestLine.getRequestResource();
            log.debug("requested resource : " + resource);

            String requestParam = requestLine.getRequestParam();

            Map<String, String> parameterMap = HttpRequestUtils.parseQueryString(requestParam);

            if (requestLine.getRequestUri().startsWith("/user/create")) {
                User user = new User(parameterMap.get("userId"), parameterMap.get("password"), parameterMap.get("name"), parameterMap.get("email"));
                users.add(user);
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + resource).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
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
