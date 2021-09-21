package fr.maxyolo01.btefranceutils.util.formatting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FormattingTest {

    @Test
    public void testBytesToHexString() {
        byte[] test = {0x0E, 0x18, 0x67, -128, -1};
        assertEquals("0e186780ff", Formatting.bytesToHexString(test));
    }

    @Test
    public void testHexStringToBytes() {
        byte[] bytes = Formatting.hexStringToBytes("0x0123456789ABCDEF");
        assertArrayEquals(new byte[] {0x01, 0x23, 0x45, 0x67, -119, -85, -51, -17}, bytes);
        bytes = Formatting.hexStringToBytes("0123456789ABCDEF");
        assertArrayEquals(new byte[] {0x01, 0x23, 0x45, 0x67, -119, -85, -51, -17}, bytes);
        bytes = Formatting.hexStringToBytes("0x0123456789abcdef");
        assertArrayEquals(new byte[] {0x01, 0x23, 0x45, 0x67, -119, -85, -51, -17}, bytes);
        bytes = Formatting.hexStringToBytes("0x01 23 45 67 89 ab cd ef");
        assertArrayEquals(new byte[] {0x01, 0x23, 0x45, 0x67, -119, -85, -51, -17}, bytes);
        assertThrows(IllegalArgumentException.class, () -> Formatting.hexStringToBytes("0"));
        assertThrows(IllegalArgumentException.class, () -> Formatting.hexStringToBytes("0xu8"));
    }

    @Test
    public void testHexColorToInt() {
        assertEquals(0xFFFF0000, Formatting.hexColorToInt("0xFF0000FF", true));
        assertEquals(0xFF0000, Formatting.hexColorToInt("0xFF0000", false));
        assertThrows(IllegalArgumentException.class, () -> Formatting.hexColorToInt("0xFF0000", true));
        assertThrows(IllegalArgumentException.class, () -> Formatting.hexColorToInt("0xFF0000FF", false));
    }

}
