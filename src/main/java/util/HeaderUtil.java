package util;

public class HeaderUtil {

    public static String getUriInHeader(String header) {
        String[] headerArr = header.split(" ");
        return headerArr[1];
    }
}
