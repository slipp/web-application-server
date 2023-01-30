package webserver;

import java.io.*;
import java.net.Socket;

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
            //헤더에서 요청url 추출
            InputStreamReader iSr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(iSr);
            String requestStr;
            requestStr = br.readLine();
            requestStr = getRequestedUrl(requestStr);
            System.out.println("requested url : " + requestStr);
            if(requestStr.equals("/favicon.ico") || requestStr == null) {
                return;
            }

            //요청된 파일 읽기
            FileReader fr = new FileReader("./webapp" + requestStr);
            BufferedReader fileBr = new BufferedReader(fr);
            String contents = readBufferedReader(fileBr);
            fileBr.close();

            byte[] body = contents.getBytes();
            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length, requestStr);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String requestUrl) {
        try {
            String contentType = "text/html";
            if(requestUrl.indexOf(".css") > 0) contentType = "text/css";

            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
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

    private String readBufferedReader(BufferedReader br) throws IOException {
        String content = "";
        String str;
        while ((str = br.readLine()) != null) {
            System.out.println(str);
            content += ( str + "\n");
        }
        br.close();
        return content;
    }

    private String getRequestedUrl(String contents) {
        String []strArr = contents.split(" ");
        System.out.println(strArr[1]);
        return strArr[1];
    }
}
