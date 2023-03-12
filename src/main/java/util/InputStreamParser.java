package util;

import java.util.Arrays;

public class InputStreamParser {
    public static String methodParse(String line) {
        try {
            return line.split(" ")[0];
        } catch (Exception e) {
            return null;
        }
    }

    public static String urlParse(String line) {
        return line.split(" ")[1].split("\\?")[0];
    }

    public static String parameterDataParse(String line) {
        try {
            return line.split(" ")[1].split("\\?")[1];
        } catch (Exception e) {
            return null;
        }
    }

    public static String versionParse(String line) {
        return line.split(" ")[2];
    }


}
