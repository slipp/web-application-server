package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
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

        // RequestHandler는 사용자의 요청이 있을 때까지 대기 상태에 있는다.
        // 요청이 있을 경우 RequestHandler 클래스에 위임하는 역할을 함.
        System.out.println("Start RequestHandler");

        // 서버 입장에서 입력 : InputStream
        // 서버 입장에서 출력 : OutputStream
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);
            String line = br.readLine();

            // 만약 null일 경우 더 진행할 의미가 없다.
            if(line == null) return;
            log.debug("header: {}", line);
            String[] tokens = line.split(" ");
            String method = tokens[0];
            String requestUrl = tokens[1];
            int length = 0;

            while(!line.equals("")) {
                line = br.readLine();
                if(line.contains("Content-Length")){
                    int index = line.indexOf(" ");
                    length = Integer.parseInt(line.substring(index+1));
                }
            }

            if (method.toLowerCase().equals("get")) {
                // method 가 get일 경우
                if(requestUrl.startsWith("/user/create")) {
                    Map<String, String> map = HttpRequestUtils.parseQueryString(requestUrl);
                    User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                    log.debug(user.toString());
                }
            }else {
                // method가 post일 경우
                if(requestUrl.startsWith("/user/create")) {
                    String sa = IOUtils.readData(br, length);
                    Map<String, String> map = HttpRequestUtils.parseQueryString(sa);
                    User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));
                    log.debug(user.toString());
                    requestUrl = "/index.html";
                    response302Header(dos, requestUrl);
                }
            }
            // http 요청 정보 중 첫 라인에 대한 검사, 요구사항1 중 2단계
            byte[] body = readFirstUrl(requestUrl);

            if(body == null) {
                log.debug("not connection page. close");
                return;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] readFirstUrl(String url) {
        byte[] body = null;
        try {
            body = Files.readAllBytes(new File("./webapp" + url).toPath());
        }catch (Exception e){
            e.getMessage();
        }
        return body;
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
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
