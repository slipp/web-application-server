package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.user.controller.UserController;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/user", new UserController());
    }

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            HttpRequest httpRequest = HttpRequest.from(in);
            HttpResponse response = HttpResponse.of(HttpStatus.NOT_FOUND, "Not Found");

            if (httpRequest.isStaticFileRequest()) {
                File file = new File("./webapp" + httpRequest.getRequestPath());
                log.debug(file.getAbsolutePath());
                response = HttpResponse.of(HttpStatus.OK, Files.readAllBytes(file.toPath()));
            } else {
                String requestPath = httpRequest.getRequestPath();
                for (String key : controllers.keySet()) {
                    if (requestPath.contains(key)) {
                        response = controllers.get(key).controll(httpRequest);
                        break;
                    }
                }
            }

            response(dos, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(DataOutputStream dos, HttpResponse response) {
        responseHeader(dos, response);
        responseBody(dos, response);
    }

    private void responseHeader(DataOutputStream dos, HttpResponse response) {
        HttpStatus status = response.getStatus();
        int lengthOfBodyContent = response.getBody().length;
        try {
            dos.writeBytes(String.format("HTTP/1.1 %d %s \r\n", status.code(), status.value()));
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            for (String header : response.getHeaders().keySet()) {
                dos.writeBytes(header + ": " + response.getHeaders().get(header) + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, HttpResponse response) {
        byte[] body = response.getBody();
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
