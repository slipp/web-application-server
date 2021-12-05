package webserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        //HttpRequest request = new HttpRequest(in);
    }

}