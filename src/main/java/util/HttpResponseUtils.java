package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.DispatcherServlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;


public class HttpResponseUtils {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);



    public static void responseHeader(DataOutputStream dos,HttpVersion version, HttpStatusCode code, List<Header> headers) {
        StringBuffer firstLine = new StringBuffer();
        firstLine.append(version.getVersion());
        firstLine.append(code.getCode());
        firstLine.append(" ");
        firstLine.append(code.getMessage());
        firstLine.append(" \r\n");

        try {
            dos.writeBytes(firstLine.toString());
            for (Header header : headers) {
                StringBuffer line = new StringBuffer();
                line.append(header.getKey());
                line.append(": ");
                line.append(header.getValue());
                line.append("\r\n");
                dos.writeBytes(line.toString());
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    public static void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
