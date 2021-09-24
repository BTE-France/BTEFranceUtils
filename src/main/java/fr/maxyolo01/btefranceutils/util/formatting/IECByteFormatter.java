package fr.maxyolo01.btefranceutils.util.formatting;

import java.util.Locale;

/**
 * Formats byte counts according to the IEC standard (base 1024).
 */
public class IECByteFormatter extends AbstractByteFormatter {

    public IECByteFormatter() {
        super(new String[] {
                "B",
                "kiB",
                "MiB",
                "GiB",
                "TiB",
                "PiB",
                "EiB"
        });
    }

    public IECByteFormatter(Locale locale) {
        this();
        this.setLocale(locale);
    }

    @Override
    public String format(long value) {
        long absB = value == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(value);
        if (absB < 1024) {
            return value + this.unitSeparator + this.units[0];
        }
        long val = absB;
        int j = 1;
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i && j++ < 6; i -= 10) {
            val >>= 10;
        }
        val *= Long.signum(value);
        return this.format(val / 1024.0, j);
    }

}
