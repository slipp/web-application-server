package webserver;

import java.io.*;
import java.net.Socket;

import controller.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class DispatcherServlet extends Thread {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private static final HandlerMapping handlerMapping = new HandlerMapping();

    private Socket connection;

    public DispatcherServlet(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String line = br.readLine();
            if (line == null || "".equals(line)) return;
            String method = HttpRequestUtils.extractMethodFromFirstLine(line);
            String path = HttpRequestUtils.extractPathFromFirstLine(line);

            handlerMapping.doService(method, path, br, dos);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
