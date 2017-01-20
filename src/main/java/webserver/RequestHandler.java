package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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
            BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String request_header = buffer_reader.readLine();

            if (request_header == null) {
                return;
            }

            String url = HttpRequestUtils.getUrl(request_header);
            // String request_method = HttpRequestUtils.getMethod(request_header);

            if (url.equals("/")) {
                url = "/index.html";
            }

            log.debug("url : {}", url);

            if (url.startsWith("/user/create")) {
                int begin_index = url.indexOf("?") + 1;
                String queryString = url.substring(begin_index);
                Map<String, String> data = HttpRequestUtils.parseQueryString(queryString);
                User user = new User(data.get("userId"), data.get("password"), data.get("name"), data.get("email"));
                log.debug("User : {}", user);
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
