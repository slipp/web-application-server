package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

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
            final byte[] response = readHTML(in);
            sendResponse(out, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] readHTML(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        byte[] body = "helloWorld".getBytes();
        String line = bufferedReader.readLine();
        while (!"".equals(line) || Objects.isNull(line)) {
            String[] tokens = line.split("\\s");
            if ("GET".equals(Arrays.stream(tokens).findFirst().get())) {
                if (tokens.length > 2 && tokens[1].contains(".html")) {
                    final String url = "./webapp" + tokens[1];
                    log.debug(url);
                    body = Files.readAllBytes(new File(url).toPath());
                    break;
                }
            }
            line = bufferedReader.readLine();
        }
        return body;
    }

    private void sendResponse(OutputStream out, byte[] response) {
        DataOutputStream dos = new DataOutputStream(out);
        response200Header(dos, response.length);
        responseBody(dos, response);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n"); // 문자열을 bytes로 변환해서 write
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
