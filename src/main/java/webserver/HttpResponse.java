package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private DataOutputStream dos;
    private Map<String, String> header;

    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
        this.header =new HashMap<>();
    }

    public void forward(String location) throws IOException {

        String fileData = new String(Files.readAllBytes(new File("./webapp" +  location).toPath()) );
        byte[] body = fileData.getBytes();

        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");

        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void forward(byte[] body) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");

        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void sendRedirect(String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: "+ location + "\r\n");

        if (!header.isEmpty()) {
            for (String key : header.keySet()) {
                dos.writeBytes(key + ": "+header.get(key)+"\r\n");
            }
        }
        dos.writeBytes("\r\n");
        dos.flush();
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


}
