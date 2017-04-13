package webserver;

/**
 * Created by woowahan on 2017. 4. 12..
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Map;
import util.HttpRequestUtils;
import util.IOUtils;

/**
 * Created by woowahan on 2017. 4. 12..
 */
public class Request {

  private Map<String, String> cookie;
  private String method;
  private int bodyLength;
  private String url;
  private Map<String, String> body;

  public static Request parse(InputStream in) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    Request request = new Request();
    boolean isParse = parseRequests(request, br);
    if (!isParse) {
      return null;
    }
    return request;
  }

  private static boolean parseRequests(Request request, BufferedReader br) throws IOException {
    String line = br.readLine();
    getMethodANDApi(request, line);
    System.out.println(line);  //
    if (line == null) {
      return false;
    }
    while (!"".equals(line)) {
      line = br.readLine();
      System.out.println(line); //
      getBodyLength(request, line);
      getCookie(request, line);
    }
    request.body = getBody(br, request.bodyLength);

    return true;
  }

  private static void getMethodANDApi(Request request, String line) {
    String[] contents = line.split(" ");
    request.method = contents[0];
    request.url = contents[1];
  }

  private static void getBodyLength(Request request, String line) {
    if (line.startsWith("Content-Length")) {
      String[] values = line.split(" ");
      request.bodyLength = Integer.parseInt(values[1]);
    }
  }

  private static void getCookie(Request request, String line) {
    if (line.startsWith("Cookie:")) {
      String[] cookieValue = line.split(" ");
      request.cookie = HttpRequestUtils.parseCookies(cookieValue[1]);
    }
  }

  private static Map<String, String> getBody(BufferedReader br, int size) throws IOException{
    if(size>0)
      return HttpRequestUtils.parseQueryString(IOUtils.readData(br, size));
    return null;
  }

  public String getUrl(){
    return this.url;
  }

  public Map<String, String> getCookie(){
    return this.cookie;
  }

  public Map<String, String> getBody(){
    return this.body;
  }

  public String getMethod(){
    return this.method;
  }
}
