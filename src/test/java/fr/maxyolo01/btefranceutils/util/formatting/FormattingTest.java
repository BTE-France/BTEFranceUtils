package fr.maxyolo01.btefranceutils.util.formatting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormattingTest {

    @Test
    public void testHexString() {
        byte[] test = {0x0E, 0x18, 0x67, -128, -1};
        assertEquals("0e186780ff", Formatting.hexString(test));
    }
}
