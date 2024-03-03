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
import webserver.http.HttpStatus;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;

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
                String requestPath = request.getPath();
                for (String key : controllers.keySet()) {
                    if (requestPath.contains(key)) {
                        response = controllers.get(key).controll(request);
                        break;
                    }
                }
            }

            response.send(dos);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
