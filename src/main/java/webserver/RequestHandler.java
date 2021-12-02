package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import static common.ParameterConstants.*;


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

            ControllerDispatcher controllerDispatcher = new ControllerDispatcher(new HttpRequest(br));
            HttpResponse httpResponse = controllerDispatcher.dispatch();

            log.debug("PATH: {}", STATIC_RESOURCES_PATH+httpResponse.getBody());

            byte[] content = new byte[0];

            if(httpResponse.getContent_Type().equals(CONTENT_TYPE_TEXT_HTML)) {
                content =
                        Files.readAllBytes(new File(STATIC_RESOURCES_PATH+httpResponse.getBody()).toPath());
            }

            responseHeader(dos, content.length, httpResponse);
            responseBody(dos, content);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseHeader(DataOutputStream dos, int lengthOfBodyContent, HttpResponse httpResponse) {
        try {
            log.debug("HTTP_VERSION: {}", HTTP_VERSION);
            log.debug("HTTP_STATUS: {}", httpResponse.getStatus());
            log.debug("HTTP_MESSAGE: {}", httpResponse.getMessage());
            log.debug("HTTP_CONTENT_TYPE: {}", httpResponse.getContent_Type());
            log.debug("HTTP_CONTENT_LENGTH: {}", lengthOfBodyContent);
            log.debug("HTTP_COOKIES: {}", httpResponse.getCookies());

            dos.writeBytes(HTTP_VERSION+" "+httpResponse.getStatus()+" "+httpResponse.getMessage()+" "+"\r\n");
            dos.writeBytes("Content-Type: "+httpResponse.getContent_Type()+"\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            if(httpResponse.getStatus().equals(HTTP_STATUS_CODE_302)) {
                dos.writeBytes("location: " + HOST+httpResponse.getBody() + "\r\n");
                log.debug("location: {}", HOST+httpResponse.getBody());
            }

            if(httpResponse.getCookies() != null) {
                dos.writeBytes("Set-Cookie: " + httpResponse.getCookies() + "\r\n");
                log.debug("cookies: {}", httpResponse.getBody());
            }
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