package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
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

            // header 출력
            while ((line = br.readLine()) != null && !line.equals("")) {
                log.debug("[Header] {}", line);
            }
            String httpMethod = requestLine[0];
            String requestUrl = requestLine[1];

            if (httpMethod.equals("GET") && requestUrl.startsWith("/user/create")) {
                int idx = requestUrl.indexOf("?");
                String queryString = requestUrl.substring(idx + 1);
                Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(queryString);
                String userId = queryStringMap.get("userId");
                String password = queryStringMap.get("password");
                String name = queryStringMap.get("name");
                String email = queryStringMap.get("email");
                User user = new User(userId, password, name, email);
                log.debug("user create {}", user);
            } else {
                byte[] body = Files.readAllBytes(new File(HTML_FILE_PATH + requestLine[1]).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
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
