package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class IOUtils {
    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    public static String readUrl(BufferedReader br, int contentLength) throws IOException {
        byte[] answer = "This is the main page".getBytes();
        String line;
        if((line = br.readLine()) != null) {
            String url = line.split(" ")[1];
            if(!"/".equals(url)) {
                answer = Files.readAllBytes(new File("./webapp" + url).toPath());
            }
        }
        return new String(answer);
    }
}
