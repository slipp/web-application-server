package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import util.InputStreamParser;
import webserver.controller.*;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private AbstractController controller;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            switch (httpRequest.getPath()) {
                case "/user/create" -> {    // 회원가입
                    controller = new CreateUserController();
                    controller.service(httpRequest,httpResponse);
                }
                case "/user/login" -> {     // 로그인
                    controller = new LoginController();
                    controller.service(httpRequest,httpResponse);
                }
                case "/user/list" -> {      // 로그인한 상태
                    controller = new ListUserController();
                    controller.service(httpRequest, httpResponse);
                }
                // 모든 경우
                default -> httpResponse.forward(httpRequest.getPath());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
