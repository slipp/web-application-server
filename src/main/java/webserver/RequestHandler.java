package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import util.InputStreamParser;

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

            String line = br.readLine();
            int i = line.indexOf("?");
            String url = InputStreamParser.urlParse(line.substring(0, i));  // index.html 파싱
            Map<String, String> httpHeader = new HashMap<>();

            while (!line.equals("")) {
                line = br.readLine();
                String[] split = line.split(":");
                try {
                    httpHeader.put(split[0].trim(), split[1].trim());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            }

            String data = IOUtils.readData(br, Integer.parseInt(httpHeader.get("Content-Length")));
            Map<String, String> map = HttpRequestUtils.parseQueryString(data);

            User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
//            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.error(e.getMessage());
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
