package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;

enum HttpMethod {
    GET, POST, PUT, DELETE
}

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private Uri uri;
    private HttpMethod method;
    private Map<String, String> headers = new HashMap<>();

    private HttpRequest(InputStream in) throws IOException {
        parseHttpRequest(in);
    }

    public static HttpRequest from(InputStream in) throws IOException {
        return new HttpRequest(in);
    }

    private void parseRequestLine(String line) {
        String[] splited = line.split(" ");
        System.out.println(Arrays.toString(splited));
        method = HttpMethod.valueOf(splited[0]);
        uri = Uri.from(splited[1]);
    }

    private void parseHeaders(List<String> headers) {
        headers.forEach(header -> {
            Pair pair = HttpRequestUtils.parseHeader(header);
            this.headers.put(pair.getKey(), pair.getKey());
        });
    }

    private void parseHttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        parseRequestLine(br.readLine());
        List<String> headers = new ArrayList<>();
        for (String line = br.readLine(); !"".equals(line) && line != null; line = br.readLine()) {
            headers.add(line);
        }
        parseHeaders(headers);
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Uri getUri() {
        return this.uri;
    }
}
