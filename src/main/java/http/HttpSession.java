package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private Map<String, Object> values = new HashMap<String, Object>();
    private String id;

    public HttpSession(String id) { // 생성자
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // 현재 세션에 value 인자로 전달되는 객체를 name 인자 이름으로 저장
    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    // 현재 세션에 name 인자로 저장되어 있는 객체 값을 찾아 반환
    public Object getAttribute(String name) {
        return values.get(name);
    }

    // 현재 세션에 name 인자로 저장되어 있는 객체 값을 삭제
    public void removeAttribute(String name) {
        values.remove(name);
    }

    // 현재 세션에 저장되어 있는 모든 값을 삭제
    public void invalidate() {
        HttpSessions.remove(id);
    }
}
