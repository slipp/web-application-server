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
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            byte[] body = "Welcome to My Page :>".getBytes();
            String[] headerInfo = readHttpHeader(br);
            String url = headerInfo[0];
            if(url.contains("html")) {
                body = Files.readAllBytes(new File("./webapp" + url).toPath());
            }
            if(url.contains("/user/create")) {
                int contentLength = Integer.parseInt(headerInfo[1]);
                String params = util.IOUtils.readData(br, contentLength);
                User user = createUser(params);
                response302Header(dos);
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String[] readHttpHeader(BufferedReader br) throws IOException{
        String line = br.readLine();
        String url = line.split(" ")[1];
        String bodyLength = "";
        while(!"".equals(line)) {
            if(line == null) break;
            if(line.contains("Content-Length")) {
                bodyLength = line.split(" ")[1];
            }
            line = br.readLine();
        }
        return new String[]{url, bodyLength};
    }

    private User createUser(String params) {
        Map<String, String> param = HttpRequestUtils.parseQueryString(params);
        String id = param.get("userId");
        String pwd = param.get("password");
        String name = param.get("name");
        String email = param.get("email");

        return new User(id, pwd, name, email);
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

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html");
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
