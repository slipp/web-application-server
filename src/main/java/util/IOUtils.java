package util;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    /**
     * @param br
     *            Request Body를 시작하는 시점이어야
     * @param contentLength
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}
