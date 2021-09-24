package fr.maxyolo01.btefranceutils.util.formatting;

import java.util.Locale;

/**
 * Formats byte counts according to the SI standard (base 1000).
 */
public final class SIByteFormatter extends AbstractByteFormatter {

    public SIByteFormatter() {
        super(new String[] {
                "B",
                "kB",
                "MB",
                "GB",
                "TB",
                "PB",
                "EB"
        });
    }

    public SIByteFormatter(Locale locale) {
        this();
        this.setLocale(locale);
    }

    @Override
    public String format(long value) {
        if (-1000 < value && value < 1000) {
            return value + this.unitSeparator + this.units[0];
        }
        int i = 1;
        while (value <= -999_950 || value >= 999_950 && i++ < 6) {
            value /= 1000;
        }
        return this.format(value / 1000.0, i);
    }

}
