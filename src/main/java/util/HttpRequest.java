package util;

import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {
    String method;
    String url;
    Map<String, String> queryString;
    Map<String, String> header;
    String body;

    public HttpRequest() {
        queryString = Maps.newHashMap();
        header = Maps.newHashMap();
    }

    public void extractHttpRequest(InputStreamReader in) throws IOException {
        BufferedReader br = new BufferedReader(in);
        String requestStr, requestUrl, requestMethod;
        String line;
        int lineNum = 1;
        while( (line = br.readLine()) != null) {
            if( line.equals("")) break;
            if( lineNum == 1) {
                String []strArr = line.split(" ");
                method = strArr[0];
                setGetUrlAndQueryString(strArr[1]);
                System.out.println("Method : " + method);
                System.out.println("Url : " + url);
                System.out.println("queryString : " + queryString.toString());
            }
            if( lineNum > 1) {
                String []kvArray = line.split(": ");
                header.put(kvArray[0], kvArray[1]);
            }
            lineNum++;
        }

        if(method.equals("POST")){
            line = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
            setPostUrlAndQueryString(line);
        }
    }

    void setGetUrlAndQueryString(String str) {
        url = str;
        //queryString 존재 시
        if(url.indexOf("?") >= 0) {
            //url과 queryString 분리
            String []strArr = str.split("\\?");
            url = strArr[0];
            //queryString 내 key-value 분리
            String []strArr2 = strArr[1].split("&");
            for(String keyValue :strArr2) {
                String []kvArr = keyValue.split("=");
                queryString.put(kvArr[0], kvArr[1]);
            }
        }
    }

    void setPostUrlAndQueryString(String str) {
        //queryString 존재 시
        if(str.indexOf("&") >= 0) {
            //queryString 내 key-value 분리
            String []strArr = str.split("&");
            for(String keyValue :strArr) {
                String []kvArr = keyValue.split("=");
                queryString.put(kvArr[0], kvArr[1]);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public Map<String, String> getHeader() {
        return header;
    }

}
