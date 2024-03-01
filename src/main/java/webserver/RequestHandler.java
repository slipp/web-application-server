package webserver;

import domain.user.controller.UserController;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            HttpRequest request = HttpRequest.from(in);
            HttpResponse response = HttpResponse.of(HttpStatus.NOT_FOUND, "Not Found");

            if (request.isStaticFileRequest()) {
                response = StaticFileController.controll(request);
            } else {
                String requestPath = request.getRequestPath();
                for (String key : controllers.keySet()) {
                    if (requestPath.contains(key)) {
                        response = controllers.get(key).controll(request);
                        break;
                    }
                }
            }

            response(dos, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response(DataOutputStream dos, HttpResponse response) throws IOException {
        responseHeader(dos, response);
        responseBody(dos, response);
    }

    private void responseHeader(DataOutputStream dos, HttpResponse response) throws IOException {
        HttpStatus status = response.getStatus();
        dos.writeBytes(String.format("HTTP/1.1 %d %s \r\n", status.code(), status.value()));
        for (String header : response.getHeaders().keySet()) {
            dos.writeBytes(header + ": " + response.getHeaders().get(header) + "\r\n");
        }
        dos.writeBytes("\r\n");
    }

    private void responseBody(DataOutputStream dos, HttpResponse response) throws IOException {
        byte[] body = response.getBody();
        if (body.length > 0) {
            dos.write(body, 0, body.length);
        }
        dos.flush();
    }
}
