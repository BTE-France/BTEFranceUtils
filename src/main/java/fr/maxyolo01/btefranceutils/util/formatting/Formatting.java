package fr.maxyolo01.btefranceutils.util.formatting;

/**
 * Utility class to format various types of data to a String representation;
 *
 * @author SmylerMC
 */
public final class Formatting {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Formatting() {} // Utility class, no instances allowed

    /**
     * Computes the hexadecimal representation of a byte array
     *
     * @param bytes - the bytes
     * @return the hexadecimal representation of the bytes
     */
    public static String hexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b: bytes) {
            int lowerBits = b & 0x0F;
            int upperBits = (b >> 4) & 0x0F;
            builder.append(HEX_DIGITS[upperBits]);
            builder.append(HEX_DIGITS[lowerBits]);
        }
        return builder.toString();
    }

}
