package fr.maxyolo01.btefranceutils.util.formatting;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

abstract class AbstractByteFormatter implements ByteFormatter {

    protected final String[] units;
    protected String unitSeparator = " ";
    private Locale locale = Locale.US;

    public AbstractByteFormatter(String[] units) {
        this.units = units;
    }

    protected String format(double value, int unit) {
        return String.format(this.locale, "%.1f%s%s", value, this.unitSeparator, this.units[unit]);
    }

    @Override
    public void setUnitSeparator(@NotNull String separator) {
        this.unitSeparator = separator;
    }

    @Override
    public void setUnitSuffix(@NotNull String suffix) {
        this.units[0] = suffix;
    }

    @Override
    public void setKiloSuffix(@NotNull String suffix) {
        this.units[1] = suffix;
    }

    @Override
    public void setMegaSuffix(@NotNull String suffix) {
        this.units[2] = suffix;
    }

    @Override
    public void setGigaSuffix(@NotNull String suffix) {
        this.units[3] = suffix;
    }

    @Override
    public void setTeraSuffix(@NotNull String suffix) {
        this.units[4] = suffix;
    }

    @Override
    public void setPetaSuffix(@NotNull String suffix) {
        this.units[5] = suffix;
    }

    @Override
    public void setExaSuffix(@NotNull String suffix) {
        this.units[6] = suffix;
    }

    @Override
    public void setLocale(@NotNull Locale locale) {
        this.locale = locale;
    }

}
