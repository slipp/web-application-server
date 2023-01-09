package util;

public class Header {
    private String key;
    private String value;

    public Header(String title, String content) {
        this.key = title;
        this.value = content;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Header{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
