package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

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
            String[] url_suffix = url.split("\\?");
            String[] param = url_suffix[1].split("&");

            ArrayList<Object> newParam = new ArrayList<>();

            for (String s : param) {
                log.info("param : {}", s);
                newParam.add(s.split("="));
            }

            for (int i = 0; i < newParam.size(); i++) {
                log.info("new param index = {}, value = {}",i, newParam.get(i));
            }

            // 만약 요청이 null 이면 종료
            if (line == null) return;

            while(!"".equals(line)){
                log.info("{}",line);
                line = br.readLine();
            }
            log.info("    ");

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
