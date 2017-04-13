package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    String httpMethod;
    String urlPath;
    
    HttpRequestHeader httpRequestHeader = new HttpRequestHeader();
    Map<String, String> requestBody = new HashMap<>();

    public HttpRequest(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        getRequestLine(br);
        getHeadersFromReader(br);
        getBodyFromReader(br);
    }

    private void getRequestLine(BufferedReader br) throws Exception {
        String line = br.readLine();

        if(line == null) {
            throw new Exception("헤더정보를 만들수 없습니다.");
        }

        this.httpMethod = HttpRequestLine.getPath(line.split(" ")[0]);
        this.urlPath = HttpRequestLine.getPath(line.split(" ")[1]);
    }

    private void getHeadersFromReader(BufferedReader br) throws IOException {
        String line;
        while (!"".equals(line = br.readLine())) {
            httpRequestHeader.add(line);
        }
    }
    
    private void getBodyFromReader(BufferedReader br) throws IOException {
        if (Optional.ofNullable(this.getHeader("Content-Length")).isPresent()) {
            setRequestBody(br, Integer.parseInt(this.getHeader("Content-Length")));
        }
    }

    private void setRequestBody(BufferedReader br, int contentLength) throws IOException {
        this.requestBody = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
    }

    public String getHeader(String headerType) {
        return httpRequestHeader.getHeader(headerType);
    }
    
    public String getHttpMethod() {
        return this.httpMethod;
    }
    
    public String getPath() {
        return this.urlPath;
    }
    
    public Map<String, String> getRequestBodies() {
        return requestBody;
    }

}
