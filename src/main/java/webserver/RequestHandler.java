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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;
import db.DataBase;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }
    private	Map<String, String>	header = new HashMap<String, String>();
    private	DataOutputStream	dos = null;
    private	Map<String, Controller>	controller = new HashMap<String, Controller>();
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	HttpRequest	request = new HttpRequest(in);
        	HttpResponse response = new HttpResponse(out);
        	String path = getDefaultPath(request.getPath());
        	controller.put("/user/create", new CreateUserController());
        	controller.put("/user/login", new LoginController());
        	controller.put("/user/list", new ListUserController());
        	Controller cntl = controller.get(request.getPath());
        	if (cntl == null)
        		response.forward(path);
        	else
        		cntl.service(request, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private	String	getDefaultPath(String path)
    {
    	if (path.equals("/"))
    		return "./index.html";
    	return path;
    }
}
