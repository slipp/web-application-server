package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

import controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            final byte[] response = readHTML(in);
            sendResponse(out, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private byte[] readHTML(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        byte[] body = "helloWorld".getBytes();
        String line = bufferedReader.readLine();
        /**
         * Header의 데이터만 읽고 있음.
         */
        while (!"".equals(line) || Objects.isNull(line)) {
            String[] tokens = line.split("\\s");
            if ("GET".equals(Arrays.stream(tokens).findFirst().get())) {
                if (tokens.length > 2 && tokens[1].contains(".html")) {
                    final String url = "./webapp" + tokens[1];
                    log.debug(url);
                    body = Files.readAllBytes(new File(url).toPath());
                } else {
                    final String requestUrl = tokens[1];
                    executeController(requestUrl);
                }
            } else {
                String[] headerToken = line.split(": ");
                if("Content-Length".equals(headerToken[0])){
                    // Body 읽기
                    String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(headerToken[1]));
                    log.debug("RequestBody: {}", requestBody);
                    // TODO: 어떤 요청에 대한 처리인지 모름 → /user/create 구별 안됨.
                    BaseController.join(requestBody);
                }
            }
            log.debug(line);
            line = bufferedReader.readLine();
        }
        return body;
    }

    public static void executeController(String request) {
        final String[] split = request.split("\\?");
        final String url = split[0];
        log.debug("requestUrl: {}", url);

        if (2 <= split.length) {
            if(url.startsWith("/user/create")){
                // TODO: 매개변수에 변수명 못 붙이나?
                BaseController.join(split[1]);
                // TODO: 만약? Index.html로 리다이렉트 하려면?
            }
        }
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
