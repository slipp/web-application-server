package controller;

import util.Header;
import util.HttpRequestUtils;
import util.HttpResponseUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.HttpStatusCode;

public class HandlerMapping {
    private static String[] staticFiles = {"css", "js", "ico", "html"};
    private Map<String, Controller> mappings;

    public HandlerMapping() {
        mappings = new HashMap<>();
        mappings.put("/index.html", new UserController());
        mappings.put("/user/create", new UserController());
        mappings.put("/user/login", new LoginController());
    }

    private Controller getController(String path) {
        return mappings.get(path);
    }

    private String getExtension(String path) {
        for (String staticFile : staticFiles) {
            String extension = path.substring(path.lastIndexOf(".")+1);
            if (extension.equals(staticFile)) return extension;
        }
        return null;
    }


    public void resourceHandler(String path,String extension, BufferedReader br, DataOutputStream dos)throws IOException {
        // body
        byte[] body = Files.readAllBytes(new File("./webapp"+path).toPath());

        // send header
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Content-Type", "text/"+extension+";charset=utf-8"));
        headers.add(new Header("Content-Length", String.valueOf(body.length)));
        HttpResponseUtils.responseHeader(dos, HttpStatusCode.OK, headers);

        // send body
        HttpResponseUtils.responseBody(dos, body);

    }

    public void doService(String method, String path, BufferedReader br, DataOutputStream dos) throws IOException {
        String extension = getExtension(path);
        // 정적 파일인 경우
        if(extension!=null){
            resourceHandler(path, extension, br,dos);
            return;
        }

        String url = HttpRequestUtils.getUrlFromPath(path);
        Controller handler = getController(url);

        // 대응되는 컨트롤러가 없는 경우
        if(handler == null){
            HttpResponseUtils.responseHeader(dos, HttpStatusCode.NOT_FOUND, new ArrayList<>());
            return;
        }

        switch (method) {
            case "GET":
                handler.get(path, br, dos);
                break;
            case "POST":
                handler.post(path, br, dos);
                break;
            default:
                HttpResponseUtils.responseHeader(dos, HttpStatusCode.NOT_FOUND, new ArrayList<>());
        }
    }
}
