package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HeaderUtil;
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
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            final String line = br.readLine();
            final String url = HeaderUtil.getUriInHeader(line);
            final String realUrl = HeaderUtil.getRealUrl(url);

            //리팩토링 힌트

//            param = HeaderUtil.getParamInUrl(url);
//            Map<String, String> map =HeaderUtil.paramToMap(param);

            if (url.startsWith("/user/create")) {
                final int index = url.indexOf("?");
                final String queryString = url.substring(index + 1);

                final Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);

                final User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
            } else {
                final DataOutputStream dos = new DataOutputStream(out);
//            byte[] body = "Hello World22".getBytes();
                final byte[] body = Files.readAllBytes(new File("./webapp" + realUrl).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (final IOException e) {
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