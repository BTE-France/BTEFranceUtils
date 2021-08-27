package fr.maxyolo01.btefranceutils.util.formatting;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteFormattingTest {

    @Test
    public void testBasicFormatting() {
        ByteFormatter si = new SIByteFormatter();
        ByteFormatter iec = new IECByteFormatter();
        assertEquals("0 B", si.format(0));
        assertEquals("0 B", iec.format(0));
        assertEquals("27 B", si.format(27));
        assertEquals("27 B", iec.format(27));
        assertEquals("999 B", si.format(999));
        assertEquals("999 B", iec.format(999));
        assertEquals("1.0 kB", si.format(1000));
        assertEquals("1000 B", iec.format(1000));
        assertEquals("1.0 kB", si.format(1023));
        assertEquals("1023 B", iec.format(1023));
        assertEquals("1.0 kB", si.format(1024));
        assertEquals("1.0 kiB", iec.format(1024));
        assertEquals("1.7 kB", si.format(1728));
        assertEquals("1.7 kiB", iec.format(1728));
        assertEquals("1.9 TB", si.format(1855425871872L));
        assertEquals("1.7 TiB", iec.format(1855425871872L));
        assertEquals("9.2 EB", si.format(Long.MAX_VALUE));
        assertEquals("8.0 EiB", iec.format(Long.MAX_VALUE));
    }

    @Test
    public void testUnitSetters() {
        ByteFormatter si = new SIByteFormatter();
        ByteFormatter iec = new IECByteFormatter();
        si.setSuffixes("O", "kO", "MO", "GO", "TO", "PO", "EO");
        iec.setSuffixes("O", "kiO", "MiO", "GiO", "TiO", "PiO", "EiO");
        assertEquals("0 O", iec.format(0));
        assertEquals("0 O", iec.format(0));
        assertEquals("1.0 kO", si.format(1000L));
        assertEquals("1.0 kiO", iec.format(1L << 10));
        assertEquals("1.0 MO", si.format(1000000L));
        assertEquals("1.0 MiO", iec.format(1L << 20));
        assertEquals("1.0 GO", si.format(1000000000L));
        assertEquals("1.0 GiO", iec.format(1L << 30));
        assertEquals("1.0 TO", si.format(1000000000000L));
        assertEquals("1.0 TiO", iec.format(1L << 40));
        assertEquals("1.0 PO", si.format(1000000000000000L));
        assertEquals("1.0 PiO", iec.format(1L << 50));
        assertEquals("1.0 EO", si.format(1000000000000000000L));
        assertEquals("1.0 EiO", iec.format(1L << 60));
    }

    @Test
    public void testFormatSetters() {
        ByteFormatter si = new SIByteFormatter(Locale.FRANCE);
        ByteFormatter iec = new IECByteFormatter(Locale.FRANCE);
        assertEquals("1,0 kB", si.format(1000L));
        assertEquals("1,0 kiB", iec.format(1L << 10));
        si.setUnitSeparator("");
        iec.setUnitSeparator("");
        assertEquals("1,0kB", si.format(1000L));
        assertEquals("1,0kiB", iec.format(1L << 10));
    }

}
