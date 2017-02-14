package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

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
        	BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        	String line=br.readLine();
        	String[] tokens = line.split(" ");
        	
        	String method = tokens[0];
        	log.debug("request line : {}", line);
        	int contentLength =0;
        	
        	for(int i=0;i<tokens.length;i++)
        	{
        		log.debug(i+"tokens : {}",tokens[i]);
        	}
        	log.debug("tokens url : {}",tokens[1]);
        	
        	if(line==null){
        		return;
        	}
        	
        	while(!line.equals("")){
        		line = br.readLine();
        		//tokens = line.split(" ");
        		if(line.contains("Content-Length"))
        			contentLength = Integer.parseInt(line.split(":")[1].trim());
        		log.debug("header : {}",line);
        	}
        	
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello World".getBytes();
            
            
            if(method.equals("GET"))
        	{
            	if(tokens[1].contains(".html")){
                	String url=tokens[1];
            		body = Files.readAllBytes(new File("./webapp" + url).toPath());
            		response200Header(dos, body.length);
                    responseBody(dos, body);
            	}
        	}
            
            if(method.equals("POST"))
            {
            	if(tokens[1].contains("/user/create")){
            		String queryString = IOUtils.readData(br, contentLength);
            		Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
            		User user = new User(params.get("userId"),params.get("password"),params.get("name"),params.get("email"));
            		DataBase.addUser(user);
            		log.debug("user : {}",user.getUserId());
            		//body = Files.readAllBytes(new File("./webapp" + "/index.html").toPath());
            		response302Header(dos,"/index.html");
            		responseBody(dos, body);
            	}
            	if(tokens[1].contains("/user/login")){
            		String queryString = IOUtils.readData(br, contentLength);
            		Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
            		log.debug("login : "+DataBase.findUserById(params.get("userId")).getPassword().toString());
            		if(DataBase.findUserById(params.get("userId")).getPassword().equals(params.get("password"))){
            			log.debug("login success");
            			response302Header(dos,"/index.html");
            			responseBody(dos, body);
            		}		
            		else{
            			log.debug("login fail");
            			response302Header(dos,"/user/login_failed.html");
            			responseBody(dos, body);
            		}
            			
            	}
            }
            
            
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
		// TODO Auto-generated method stub
    	try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: "+url+" \r\n");
        
            dos.writeBytes("\r\n");
            log.debug("302");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
	}
    
    private void login(DataOutputStream dos){
    	try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Set-Cookie: logined=true \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
