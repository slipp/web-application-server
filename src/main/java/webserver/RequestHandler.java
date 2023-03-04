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

            String line = br.readLine();
            String url = InputStreamParser.urlParse(line);  // index.html 파싱
            System.out.println(url);
            Map<String, String> httpHeader = new HashMap<>();

            while (!line.equals("")) {
                line = br.readLine();
                String[] split = line.split(":");
                try {
                    httpHeader.put(split[0].trim(), split[1].trim());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = new byte[0];


            switch (url) {
                case "/user/create" -> {    // 회원가입
                    String data = IOUtils.readData(br, Integer.parseInt(httpHeader.get("Content-Length")));
                    Map<String, String> map = HttpRequestUtils.parseQueryString(data);
                    DataBase.addUser(new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email")));

                    // index로 이동
                    dos = new DataOutputStream(out);
                    response302Header(dos, "/index.html");
                }
                case "/user/login" -> {     // 로그인
                    String data = IOUtils.readData(br, Integer.parseInt(httpHeader.get("Content-Length")));
                    Map<String, String> map = HttpRequestUtils.parseQueryString(data);
                    User user = DataBase.findUserById(map.get("userId"));

                    if (user != null && Objects.equals(user.getPassword(), map.get("password"))) {
                        // 로그인 성공
                        dos = new DataOutputStream(out);
                        response302HeaderWithCookie(dos, "/index.html", "logined=true");
                    } else {
                        // 로그인 실패
                        dos = new DataOutputStream(out);
                        body = Files.readAllBytes(new File("./webapp/user/login_failed.html").toPath());
                        response302HeaderWithCookie(dos, "/user/login_failed.html", "logined=false");
                    }
                }
                case "/user/list" -> {      // 로그인한 상태
                    try {
                        Map<String, String> cookie = HttpRequestUtils.parseCookies(httpHeader.get("Cookie"));
                        if (Boolean.parseBoolean(cookie.get("logined"))) {
                            // 로그인 한 상태
                            int idx = 3;

                            Collection<User> userList = DataBase.findAll();

                            StringBuilder sb = new StringBuilder();
                            for(User user : userList) {
                                sb.append("<tr>");
                                sb.append("<th scope=\"row\">"+idx+"</th><td>"+user.getUserId()+"</td> <td>"+user.getName()+"</td> <td>"+user.getEmail()+"</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td></tr>");
                                idx++;
                            }

                            String fileData = new String(Files.readAllBytes(new File("./webapp" + url + ".html").toPath()) );
                            fileData = fileData.replace("%user_list%", URLDecoder.decode(sb.toString(), "UTF-8"));

                            body = fileData.getBytes();
                            dos = new DataOutputStream(out);

                            if (httpHeader.get("Accept").contains("text/css")) {
                                response200HeaderWithCss(dos, body.length);
                            } else {
                                response200Header(dos, body.length);
                            }
                        } else {
                            dos = new DataOutputStream(out);
                            response302Header(dos, "/index.html");
                        }
                    } catch (NullPointerException e) {
                        // 로그인 안된 상태
                        body = Files.readAllBytes(new File("./webapp/index.html").toPath());
                        dos = new DataOutputStream(out);
                        response302Header(dos, "/index.html");
                    }
                }
                // 모든 경우
                default -> {
                    body = Files.readAllBytes(new File("./webapp" + url).toPath());
                    dos = new DataOutputStream(out);

                    if (httpHeader.get("Accept").contains("text/css")) {
                        response200HeaderWithCss(dos, body.length);
                    } else {
                        response200Header(dos, body.length);
                    }
                }
            }

            responseBody(dos, body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: "+ location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: "+ location + "\r\n");
            dos.writeBytes("Set-Cookie: "+cookie+"\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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
