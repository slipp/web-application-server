package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import controller.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

/**
 * Note: 참고할  RequestHandler: com.sun.tools.sjavac.server.RequestHandler
 */
public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket socket;

    public RequestHandler(Socket connectionSocket) {
        this.socket = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", socket.getInetAddress(),
                socket.getPort());
        // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             OutputStream out = socket.getOutputStream()) {

            // Request Head를 분석
            final HttpRequest request = setRequestHeader(bufferedReader);
            log.info(request.toString());
            // TODO: ViewResolver 생성
            byte[] body = createByteData(request);
            sendResponse(out, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] createByteData(HttpRequest request) throws IOException {
        byte[] body = "helloWorld".getBytes();

        if (RequestLine.HttpMethod.GET.name().equals(request.getRequestLine().getMethod())) {
            if (request.getRequestLine().getUrl().contains(".html")) {
                log.info(request.getRequestLine().getUrl());
                final String url = "./webapp" + request.getRequestLine().getUrl();
                log.debug(url);
                // TODO: 정의하지 않은 index // home으로 => 아파치단에서
                // todo: 여기에서 view하지말기(이건 임시)
                body = Files.readAllBytes(new File(url).toPath());
            } else {
                executeGetController(request);
            }
        } else if(RequestLine.HttpMethod.POST.name().equals(request.getRequestLine().getMethod())){
            executePostController(request);
        }
        return body;
    }

    private HttpRequest setRequestHeader(BufferedReader bufferedReader) throws IOException {
        return new HttpRequestDefault(bufferedReader);
    }

    /**
     * Get을 위한 컨트롤러(임시)
     * @param request
     */
    public static void executeGetController(HttpRequest request) {
        final String[] split = request.getRequestLine().getUrl().split("\\?");
        final String url = split[0];
        log.debug("requestUrl: {}", url);

        if (Objects.nonNull(request)) {
            if (url.startsWith("/user/create")) {
                // Note: Get일 경우, QueryString 파싱하는 부분만 다름. => 데이터가 QueryString에 있다.
                final Map<String, String> data = HttpRequestUtils.parseQueryString(split[1]);
                UserService.join(data);
            }
        }
    }

    public static void executePostController(HttpRequest request) {
        final String url = request.getRequestLine().getUrl();
        log.debug("requestUrl: {}", url);

        if (Objects.nonNull(request)) {
            if (url.startsWith("/user/create")) {
                // Note: 데이터가 Body에 있다.
                UserService.join(request.getRequestBody());
            }
        }
    }

    private void sendResponse(OutputStream out, byte[] response) {
        DataOutputStream dos = new DataOutputStream(out);
        response200Header(dos, response.length);
        responseBody(dos, response);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n"); // 문자열을 bytes로 변환해서 write
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
