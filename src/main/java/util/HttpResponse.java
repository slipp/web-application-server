package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.*;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    OutputStream out;
    DataOutputStream dos;
    byte[] body;

    public HttpResponse(OutputStream outputStream) {
        out = outputStream;
        dos = new DataOutputStream(out);
    }

    public void forward(String url) throws IOException {
        String contents = "";
        File file = new File("./webapp" + url);
        if (!url.equals("") && file.exists()) {
            FileReader fr = new FileReader("./webapp" + url);
            BufferedReader br = new BufferedReader(fr);
            String str;
            while ((str = br.readLine()) != null) {
                System.out.println(str);
                contents += str;
            }
            br.close();
        }
        body = contents.getBytes();

        try {
            String contentType = "text/html";
            if(url.indexOf(".css") > 0) contentType = "text/css";
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");

            /*
            if ( !cookie.isEmpty() ) {
                dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            }
            */
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String s, String s1) throws IOException {
        try {
            dos.writeBytes(s + ": " + s1 + " \r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void response() {
        try {
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            if (body != null) {
                dos.write(body, 0, body.length);
            }
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
