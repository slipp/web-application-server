package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    Map<String, String> requestHeaders = new HashMap<>();

    public HttpRequest(BufferedReader br) throws Exception {
        String line = br.readLine();

        if(line != null) {
            requestHeaders.put("Http_Method", line.split(" ")[0]);
            requestHeaders.put("Target_Url", line.split(" ")[1]);
            log.debug("Http_Method : {}", line.split(" ")[0]);
            log.debug("Target_Url : {}", line.split(" ")[1]);
    
            while (!"".equals(line)) {
                line = br.readLine();
                if(!"".equals(line)) {
                    requestHeaders.put(HttpRequestUtils.parseHeader(line).getKey(),HttpRequestUtils.parseHeader(line).getValue());
                    log.debug("{} : {}", HttpRequestUtils.parseHeader(line).getKey(), HttpRequestUtils.parseHeader(line).getValue());
                }
            }
        } else {
            throw new Exception("헤더정보를 만들수 없습니다.");
        }
    }
    
    public String getHeader(String headerType) {
        return requestHeaders.get(headerType);
    }
    
    public String getHttpMethod() {
        return requestHeaders.get("Http_Method");
    }
    
    public String getPath() {
        return requestHeaders.get("Target_Url");
    }
}
