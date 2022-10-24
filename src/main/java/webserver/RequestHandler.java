package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.file.Files;
import java.util.stream.Stream;

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

            // ??
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String r;
            byte[] bytes;

            if ((r = bufferedReader.readLine()) != null) {
                log.info(r);
                String[] split = r.split(" ");
                if (split[1].equals("/index.html")) {
                    log.info("index.html 요청입니다.");

                    // 파일읽기
                    bytes = Files.readAllBytes(new File(("./webapp" + split[1])).toPath());
                    DataOutputStream dos = new DataOutputStream(out);
                    response200Header(dos, bytes.length);
                    responseBody(dos, bytes);
                }else{
                    // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
                    DataOutputStream dos = new DataOutputStream(out);
                    byte[] body = "HelloWorld".getBytes();
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
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
