package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String text) {
        if(text == null || text.isEmpty())
            return 0;
        String[] numbers = preprocessor(text);
        return sum(numbers);
    }

    private String[] preprocessor(String text) {
        String Separators = ",|:";
        Matcher m = Pattern.compile("//(.*)\n(.*)").matcher(text);
        if(m.find()) {
            String customs = m.group(1);
            Separators = Separators + "|" + customs;
            text = m.group(2);
        }
        return text.split(Separators);
    }

    private int sum(String[] numbers) {
        int total = 0;
        for(String number : numbers) {
            total += stringToInt(number);
        }
        return total;
    }

    private int stringToInt(String num) {
        int number = Integer.parseInt(num);
        if(number < 0) {
            throw new RuntimeException("음수입니다.");
        }
        return number;
    }
}
