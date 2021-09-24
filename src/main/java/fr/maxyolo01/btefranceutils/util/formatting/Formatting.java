package fr.maxyolo01.btefranceutils.util.formatting;

import java.util.Arrays;

/**
 * Utility class to format various types of data to a String representation;
 *
 * @author SmylerMC
 */
public final class Formatting {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Formatting() {} // Utility class, no instances allowed

    public static String escapeMarkdown(CharSequence sequence) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if ("\\`*_{}[]()#+-.!".contains("" + c)) {
                builder.append('\\');
            }
            builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Computes the hexadecimal representation of a byte array
     *
     * @param bytes - the bytes
     * @return the hexadecimal representation of the bytes
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b: bytes) {
            int lowerBits = b & 0x0F;
            int upperBits = (b >> 4) & 0x0F;
            builder.append(HEX_DIGITS[upperBits]);
            builder.append(HEX_DIGITS[lowerBits]);
        }
        return builder.toString();
    }

    /**
     * Deserializes a hex string to the bytes it represents.
     * The bytes are case insensitive.
     * The hex string may start with a 0x prefix (case sensitive).
     *
     * @param hexString - hex string to decode
     * @return a byte array that corresponds to the string that was passed as an argument.
     * @throws NullPointerException if the given string is null
     * @throws IllegalArgumentException if the given string has an odd number of digits or contains invalid digits
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null) throw new NullPointerException("hex string cannot be null");
        hexString = hexString.toLowerCase().replaceAll(" ", "");
        if (hexString.startsWith("0x")) hexString = hexString.substring(2);
        if (hexString.length() % 2 == 1) throw new IllegalArgumentException("Odd number of hex digits in hex string");
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0, j = 0; j < bytes.length; i += 2, j++) {
            int x1 = Arrays.binarySearch(HEX_DIGITS, hexString.charAt(i));
            int x2 = Arrays.binarySearch(HEX_DIGITS, hexString.charAt(i + 1));
            if (x1 < 0 || x2 < 0) throw new IllegalArgumentException("Unknown digit");
            bytes[j] = (byte)(
                    ((x1 << 4) & 0xF0) +
                    (x2 & 0x0F)
            );
        }
        return bytes;
    }

    /**
     * Decodes an RGBA hex string to an int.
     * Format with alpha: "AARRGGBB".
     * Format without alpha: "RRGGBB".
     * The "0x" prefix may be added.
     *
     * @param color - hex representation of the color
     * @param alpha - whether or not alpha is expected
     * @return the integer corresponding to the given color, with the format 0xAARRGGBB or 0xRRGGBB if alpha is not expected
     * @throws IllegalArgumentException if the color hex string is not valid
     */
    public static int hexColorToInt(String color, boolean alpha) {
        byte[] bytes = hexStringToBytes(color);
        if (alpha && bytes.length == 4) {
            return (Byte.toUnsignedInt(bytes[3]) << 24) +
                   (Byte.toUnsignedInt(bytes[0]) << 16) +
                   (Byte.toUnsignedInt(bytes[1]) << 8) +
                    Byte.toUnsignedInt(bytes[2]);
        } else if (!alpha && bytes.length == 3) {
            return (Byte.toUnsignedInt(bytes[0]) << 16) +
                   (Byte.toUnsignedInt(bytes[1]) << 8) +
                    Byte.toUnsignedInt(bytes[2]);
        } else {
            throw new IllegalArgumentException("Invalid number of bytes in color string");
        }
    }

}
