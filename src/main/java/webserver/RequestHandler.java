package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.DosFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import javax.sound.sampled.Line;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            String firstLine = br.readLine();
            log.debug("{}", firstLine);
            String url = HttpRequestUtils.getUrl(firstLine);
            printHttpRequest(br);
            handleRequestUrl(url, dos);
            //byte[] body = "Hello World".getBytes();
            //response200Header(dos, body.length);
            //responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void printHttpRequest(BufferedReader br) throws IOException {
        String line;
        while((line = br.readLine()) != null && !line.isEmpty()) {
            log.debug("{}", line);
        }
    }

    private void handleRequestUrl(String url, DataOutputStream out) throws IOException {
        if(url.equals("/")) {
            handleRootPage(url, out);
            return;
        }
        if(url.equalsIgnoreCase("/index.html")) {
            handleIndexPage(url, out);
            return;
        }
    }
    private void handleRootPage(String url, DataOutputStream out) {
        byte[] body = "Hello World".getBytes();
        response200Header(out, body.length);
        responseBody(out, body);
    }
    private void handleIndexPage(String url, DataOutputStream out) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url.toLowerCase()).toPath());
        response200Header(out, body.length);
        out.write(body);
        out.flush();
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
