package util;
public enum HttpVersion {
    HTTP_V1("http/1.1"), HTTP_V2("http/2.0");
    private String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
