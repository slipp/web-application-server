package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        	InputStreamReader ir = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(ir);
            String line = br.readLine();
            if (line == null) {
                return;
            }
            
            // 요청 url 을 잘라 파일 경로르 추춣한다.
            String[] tokens = line.split(" ");
            String url = tokens[1];
            log.info("URL: {}",url);

            // url 에서 파라미터들을 분리한다. ?name=hi&age=14 이렇게 되어있기에 파라미터는 index = 1에 저장된다.
            String[] url_suffix = url.split("\\?");

            // 파라미터가 있을 때의 조건
            if (url_suffix.length > 1) {
                String[] param = url_suffix[1].split("&");

                // 이제 파라미터들이 ['name=hi','age=14'] 이렇게 들어가 있다.
                // 이제 이것을 '=' 으로 나눠서 termpList 에 넣자
                // 그리고 이 데이터를 다시 Map 데이터에 세팅하자.
                Map<String, Object> map = new HashMap<>();
                for (String s : param) {
                    log.info("param : {}", s);
                    String[] tempList = s.split("=");
                    if (tempList.length > 1) {
                        map.put(tempList[0], tempList[1]);
                    }else{
                        map.put(tempList[0], "");
                    }
                }
                log.info("map : {}", map);
                String userId = (String) map.get("userId");
                String password = (String) map.get("password");
                String name = (String) map.get("name");
                String email = (String) map.get("email");

                User user = new User(userId, password, name, email);
            }

            // 만약 요청이 null 이면 종료
            if (line == null) return;

            while(!"".equals(line)){
                log.info("{}",line);
                line = br.readLine();
            }

            line = br.readLine();
            log.info("{}",line);
            line = br.readLine();
            log.info("{}",line);

            // OutStream을 통해 응답 출력
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            log.info("Path: {}",new File("./webapp" + url).toPath());

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
