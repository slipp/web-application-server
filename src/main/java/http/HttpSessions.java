package http;

import java.util.HashMap;
import java.util.Map;

// 모든 세션에 대한 저장소 클래스
// 세션 ID를 이용하여 세션을 생성하고 조회하며 삭제하는 기능을 제공함.
public class HttpSessions {
    private static Map<String, HttpSession> sessions
            = new HashMap<String, HttpSession>();

    // 주어진 세션 ID(String)에 해당하는 HttpSession 객체 반환
    public static HttpSession getSession(String id) {
        HttpSession session = sessions.get(id);

        if (session == null) { // 만약 해당 ID의 세션이 존재하지 않으면
            session = new HttpSession(id); // 새로운 HttpSession 객체를 생성하고,
            sessions.put(id, session); // 이를 sessions 맵에 추가함.
            return session; // 새로 생성된 세션 반환
        }

        return session; // 이미 존재하는 경우 현재의 세션 반환
    }

    static void remove(String id) {
        sessions.remove(id);
    }
}
