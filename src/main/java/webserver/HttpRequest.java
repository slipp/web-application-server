package webserver;

import util.HttpRequestUtils;
import util.InputStreamParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private Map<String, String> httpHeader = new HashMap<>();
    private Map<String, String> parameter = new HashMap<>();
    private String method;
    private String path;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        this.method = InputStreamParser.methodParse(line);
        this.path = InputStreamParser.urlParse(line);

        if (method.equals("GET")) {
            String parameter = InputStreamParser.parameterDataParse(line);
            if (parameter!=null) {
                this.parameter = HttpRequestUtils.parseQueryString(parameter);
            }
        }


        while (line!=null && !line.equals("")) {    // header
            line = br.readLine();

            if (line==null) break;

            String[] split = line.split(":");
            try {
                httpHeader.put(split[0].trim(), split[1].trim());
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
        }


        if (line != null && method.equals("POST")) {
        line = br.readLine();
            this.parameter = HttpRequestUtils.parseQueryString(line);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String key) {
        return httpHeader.get(key);
    }

    public String getParameter(String key) {
        return parameter.get(key);
    }
}
