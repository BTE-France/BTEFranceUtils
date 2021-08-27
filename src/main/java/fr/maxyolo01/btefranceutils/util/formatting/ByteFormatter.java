package fr.maxyolo01.btefranceutils.util.formatting;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * A class to format memory byte counts to human readable strings.
 *
 * @author SmylerMC
 */
public interface ByteFormatter {

    /**
     * Formats the given byte count to a human readable string.
     *
     * @param value - a byte count
     * @return the value formatted
     */
    String format(long value);

    /**
     * Sets the locale used when formatting numbers with this {@link ByteFormatter}
     *
     * @param locale - the {@link Locale}
     */
    void setLocale(@Nonnull Locale locale);

    /**
     * Sets string used to separate the unit (e.g. kiO), from the number. This is usually just a space.
     *
     * @param separator - the separator to use
     */
    void setUnitSeparator(@Nonnull String separator);

    /**
     * Sets the unit suffix to use for the base unit (e.g. B).
     *
     * @param suffix - the suffix
     */
    void setUnitSuffix(@Nonnull String suffix);

    /**
     * Sets the unit suffix for the second kilo unit (e.g. kB).
     *
     * @param suffix - the suffix
     */
    void setKiloSuffix(@Nonnull String suffix);

    /**
     * Sets the unit suffix for the mega unit (e.g. MB).
     *
     * @param suffix - the suffix
     */
    void setMegaSuffix(@Nonnull String suffix);

    /**
     * Sets the unit suffix for the giga unit (e.g. GB).
     *
     * @param suffix - the suffix
     */
    void setGigaSuffix(@Nonnull String suffix);

    /**
     * Sets the unit suffix for the tera unit (e.g. TB).
     *
     * @param suffix - the suffix
     */
    void setTeraSuffix(@Nonnull String suffix);

    /**
     * Sets the unit suffix for the peta unit (e.g. PB).
     *
     * @param suffix - the suffix
     */
    void setPetaSuffix(@Nonnull String suffix);

    /**
     * Sets the unit suffix for the exa unit (e.g. EB).
     *
     * @param suffix - the suffix
     */
    void setExaSuffix(@Nonnull String suffix);

    /**
     * Sets all unit suffixes at once.
     *
     * @param unit - the base unit suffix
     * @param kilo - the kilo unit suffix
     * @param mega - the mega unit suffix
     * @param giga - the giga unit suffix
     * @param tera - the tera unit suffix
     * @param peta - the peta unit suffix
     * @param exa - the exa unit suffix
     */
    default void setSuffixes(
            @Nonnull String unit,
            @Nonnull String kilo,
            @Nonnull String mega,
            @Nonnull String giga,
            @Nonnull String tera,
            @Nonnull String peta,
            @Nonnull String exa) {
        this.setUnitSuffix(unit);
        this.setKiloSuffix(kilo);
        this.setMegaSuffix(mega);
        this.setGigaSuffix(giga);
        this.setTeraSuffix(tera);
        this.setPetaSuffix(peta);
        this.setExaSuffix(exa);
    }

}
