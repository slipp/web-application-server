import org.junit.jupiter.api.Test;
import util.HttpRequestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest {

    @Test
    void extract_queryString_from_requestUrl() {
        // given
        String requestUrl = "/user/create?userId=jwkim.oa&password=1234&name=JiWon&email=jwkim.oa@gmail.com";

        // when
        int idx = requestUrl.indexOf("?");
        String queryString = requestUrl.substring(idx + 1);
        Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(queryString);

        // then
        assertThat(queryStringMap.get("userId")).isEqualTo("jwkim.oa");
        assertThat(queryStringMap.get("password")).isEqualTo("1234");
        assertThat(queryStringMap.get("name")).isEqualTo("JiWon");
        assertThat(queryStringMap.get("email")).isEqualTo("jwkim.oa@gmail.com");
    }
}
