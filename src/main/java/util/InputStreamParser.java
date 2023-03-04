package util;

import java.util.Arrays;
import java.util.Map;

public class InputStreamParser {
    public static String urlParse(String in) {
        String[] lineToWord = in.split(" ");
        return Arrays.stream(lineToWord)
                .filter(s -> s.matches("/.+"))
                .findFirst()
                .get();
    }

}
