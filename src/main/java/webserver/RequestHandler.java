package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
            log.info("connection: {}", connection.toString());

            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            String line=br.readLine();

            if("".equals(line) || line==null){
                log.info("line is null, return");
                return;
            }

            while(!"".equals(line) && line!=null){
                log.info("line: {}", line);

                if(isIndexPage(line)){
                    DataOutputStream dos=new DataOutputStream(out);
                    byte[] body= Files.readAllBytes(Paths.get("./webapp/index.html"));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }

                line=br.readLine();
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello World".getBytes();
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    // index 페이지를 요청하는지 확인
    private boolean isIndexPage(String line){

        String[] tokens=line.split(" ");

        if("GET".equals(tokens[0]) && "/index.html".equals(tokens[1])) {
            return true;
        }

        return false;
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
