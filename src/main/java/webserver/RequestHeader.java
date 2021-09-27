package webserver;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHeader {
    public static RequestHeader NULL_OBJECT = new RequestHeader(){
        @Override
        public boolean isValid() {
            return false;
        }
    };

    // Title Enum값이 없는 key가 있을 수도 있어서, 사용편의성을 위해 get할때만 ENUM값으로
    private Map<String, String> header = new ConcurrentHashMap<>();

    public void put(String key, String value){
        header.put(key, value);
    }

    public String get(String key){
        return header.get(key);
    }

    public String get(HttpHeader key){
        return header.get(key.getKey());
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public boolean isValid(){
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("\n");
        Iterator<Map.Entry<String, String>> iterator = getHeader().entrySet().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().toString() + "\n");
        }
        return "RequestHeader {" + stringBuilder.toString() + '}';
    }
}
