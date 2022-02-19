package util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HeaderUtilTest {

    @Test
    public void url_분리() {

        String header = "GET /index.html HTTP/1.1";

        String url = HeaderUtil.getUriInHeader(header);

        assertThat("/index.html", is(url));
    }
}
