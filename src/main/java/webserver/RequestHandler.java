package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            BufferedReader br = new BufferedReader(iSr);
            String requestStr, requestUrl, requestMethod;
            requestStr = br.readLine();
            requestMethod = getRequestMethod(requestStr);
            System.out.println("requestMethod : " + requestMethod);

            requestUrl = getRequestedUrl(requestStr);
            System.out.println("requested url : " + requestUrl);
            if(requestUrl.equals("/favicon.ico") || requestUrl == null) {
                return;
            }
            boolean loginSuccess = false;

            //Cookie 저장
            String line = "";
            String cookie = "";
            while (requestMethod.equals("GET") && (line = br.readLine()) != null) {
                if( line.equals("")) break;
                if( line.indexOf("Cookie") >= 0) {
                    String []strArr = line.split(" ");
                    cookie = strArr[1];
                    System.out.println("Cookie : " + cookie);

                    if(cookie.equals("logined=true")) {
                        loginSuccess = true;
                    }
                }
            }


            String contents = "";
            String params = "";
            //요청된 url이 GET이면 ? 찾아서 파라미터 설정
            if( requestMethod.equals("GET") && requestUrl.indexOf("?") >= 0 ) {
                params = requestUrl.substring(requestUrl.indexOf("?"));
                requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
            }

            //요청된 url이 POST이면 body에서 찾아서 파라미터 설정
            if( requestMethod.equals("POST") ) {
                String str;
                int contentLength = 0;
                //Header에서 Content-Length 구하기
                while ((str = br.readLine()) != null) {
                    if(str.equals("")) break;
                    System.out.println(str);
                    if(str.indexOf("Content-Length") >= 0 ) {
                        String []values = str.split(" ");
                        contentLength = Integer.parseInt(values[1]);
                        System.out.println(contentLength);
                    }
                }
                //Body에서 parameters 구하기
                params = readData(br, contentLength);
                System.out.println(params);

                //br.close();
            }

            System.out.println("requested url2 : " + requestUrl);

            User user;
            //회원가입 요청이면 파라미터 정보 저장
            if(!params.isEmpty() && requestUrl.equals("/user/create")) {
                String []paramArr = params.split("&");
                String userId = paramArr[0].substring(paramArr[0].indexOf("=")+1);
                String password = paramArr[1].substring(paramArr[1].indexOf("=")+1);
                String name = paramArr[2].substring(paramArr[2].indexOf("=")+1);
                String email = paramArr[3].substring(paramArr[3].indexOf("=")+1);

                user = new User(userId, password, name, email);
                System.out.println(user.toString());

                DataBase.addUser(user);
            }

            //로그인 요청이면 비밀번호 검증
            if(!params.isEmpty() && requestUrl.equals("/user/login")) {
                String []paramArr = params.split("&");
                String userId = paramArr[0].substring(paramArr[0].indexOf("=")+1);
                String password = paramArr[1].substring(paramArr[1].indexOf("=")+1);

                //User user = new User(userId, password, name, email);
                System.out.println("login User : " + userId + " Password : " + password);

                User registeredUser = DataBase.findUserById(userId);
                if ( registeredUser.getPassword().equals(password) ) {
                    loginSuccess = true;
                    System.out.println("login Success!!");
                }
            }

            //요청된 파일 읽기
            if( requestMethod.equals("GET") && requestUrl.indexOf(".") >= 0) {
                contents = readFile(requestUrl);
            }

            if( requestMethod.equals("GET") && requestUrl.equals("/user/list") && loginSuccess == true) {
                StringBuilder sb = new StringBuilder();
                Collection<User> userList = (Collection<User>) DataBase.findAll();
                for(User item : userList) {
                    sb.append("<h3>");
                    sb.append(item.getUserId());
                    sb.append(" : ");
                    sb.append(item.getName());
                    sb.append("</h3>");
                }
                contents = sb.toString();
                System.out.println("list : " + contents);
            }

            byte[] body = contents.getBytes();
            DataOutputStream dos = new DataOutputStream(out);

            if(requestMethod.equals("POST") && requestUrl.equals("/user/create")) {
                response302Header(dos, "/index.html");
            }

            if(requestMethod.equals("POST") && requestUrl.equals("/user/login")) {
                if( !loginSuccess ) {
                    response302Header(dos, "/user/login_failed.html");
                }

                if( loginSuccess ) {
                    responseLoginSuccess302Header(dos);
                }
            }

            if(requestMethod.equals("GET")) {
                response200Header(dos, body.length, requestStr, "");
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String readFile(String requestUrl) throws IOException {
        String contents = "";
        File file = new File("./webapp" + requestUrl);
        if (file.exists()) {
            FileReader fr = new FileReader("./webapp" + requestUrl);
            BufferedReader fileBr = new BufferedReader(fr);
            contents = readBufferedReader(fileBr);
            fileBr.close();
        }

        if (!file.exists()) {
            contents = "Singed up successfully!";
        }
        return contents;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String requestUrl, String cookie) {
        try {
            String contentType = "text/html";
            if(requestUrl.indexOf(".css") > 0) contentType = "text/css";
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            if ( !cookie.isEmpty() ) {
                dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLoginSuccess302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
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
            content += str;
        }
        br.close();
        return content;
    }

    private String getRequestedUrl(String contents) {
        String []strArr = contents.split(" ");
        System.out.println(strArr[1]);
        return strArr[1];
    }

    private String getRequestMethod(String contents) {
        String []strArr = contents.split(" ");
        System.out.println(strArr[0]);
        return strArr[0];
    }
}
