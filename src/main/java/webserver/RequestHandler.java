package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import controller.*;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpResponse;

import static util.IOUtils.readData;

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
            HttpRequest httpRequest = new HttpRequest();
            HttpResponse httpResponse = new HttpResponse(out);
            httpRequest.extractHttpRequest(iSr);
            System.out.println("httpRequest : " + httpRequest.toString());
            if(httpRequest.getUrl().equals("/favicon.ico") || httpRequest.getUrl() == null) {
                return;
            }

            Map<String, Controller> controllerMap = Maps.newHashMap();
            controllerMap.put("/user/create", new CreateUserController());
            controllerMap.put("/user/login", new LoginController());
            controllerMap.put("/user/list", new ListUserController());
            controllerMap.put("file", new AbstractController());

            Controller controller;
            if( httpRequest.getUrl().indexOf(".") >= 0) {
                controller = controllerMap.get("file");
            } else {
                controller = controllerMap.get(httpRequest.getUrl());
            }
            controller.service(httpRequest, httpResponse);
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
