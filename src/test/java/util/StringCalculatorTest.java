package util;

import junit.framework.TestCase;
import org.junit.Before;

public class StringCalculatorTest extends TestCase {
    private StringCalculator cal;

    @Before
    public void setUp() {
        cal = new StringCalculator();
    }

    public void testAdd() {
        assertEquals(0, cal.add(""));
        assertEquals(0, cal.add(null));
        assertEquals(1, cal.add("1"));
        assertEquals(6, cal.add("1,2:3"));
        assertEquals(10, cal.add("//;\n1;2,3:4"));

    }
}