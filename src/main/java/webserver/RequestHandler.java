package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            byte[] body = "Welcome to My Page :>".getBytes();
            String line;

            if((line = br.readLine()) != null) {
                String url = readUrl(br, line);
                if(!"/".equals(url)) {
                    String path = url;
                    Matcher m = Pattern.compile("(.*)\\?(.*)").matcher(url);
                    if(m.find()) {
                        path = m.group(1);
                        String params = m.group(2);
                        User user = createUser(params);
                    }
                    body = Files.readAllBytes(new File("./webapp" + path).toPath());
                }
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String readUrl(BufferedReader br, String line) throws IOException{
        String url = line.split(" ")[1];
        while(!"".equals(line)) {
            if(line == null) break;
            line = br.readLine();
        }

        return url;
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
