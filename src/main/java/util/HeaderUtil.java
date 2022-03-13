package util;


import java.util.HashMap;
import java.util.Map;

public class HeaderUtil {

    public static String getUriInHeader(String header) {
        String[] headerArr = header.split(" ");
        return headerArr[1];
    }

    public static String getRealUrl(String url) {
        int index = url.indexOf("?");

        if (index == -1) {
            return url;
        }
        return url.substring(0, index);
    }

    public static String getParamInUrl(String url) {
        int index = url.indexOf("?");

        if (index == -1) {
            return "";
        }

        return url.substring(index+1);
    }

    public static Map<String, String> paramToMap(String paramText) {
        int length = paramText.length();
        String[] paramArr = paramText.split("&");
        Map<String, String> paramMap = new HashMap<>();

        if (length == 0) {
            return paramMap;
        }

        for (String param : paramArr) {
            mapInputParam(param, paramMap);
        }

        System.out.println(paramMap);

        return paramMap;
    }

    private static void mapInputParam (String param, Map<String, String> paramMap) {
        int index = param.indexOf("=");
        if(index > -1) {
            String key = param.substring(0, index);
            String value = param.substring(index + 1, param.length());

            paramMap.put(key, value);
        }
    }

}
