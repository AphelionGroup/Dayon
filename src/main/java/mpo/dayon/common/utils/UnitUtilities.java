package mpo.dayon.common.utils;

public abstract class UnitUtilities {
    private final static String DEC_UNIT = "%.2f %s";

    public enum BitUnit {
        Kbit("Kbit", "kilo", Math.pow(10, 3)),
        Mbit("Mbit", "mega", Math.pow(10, 6)),
        Gbit("Gbit", "giga", Math.pow(10, 9)),
        Tbit("Tbit", "tera", Math.pow(10, 12)),
        Pbit("Pbit", "peta", Math.pow(10, 15)),
        Ebit("Ebit", "exa", Math.pow(10, 18)),
        Zbit("Zbit", "zetta", Math.pow(10, 21)),
        Ybit("Ybit", "yotta", Math.pow(10, 24));

        private final String symbol;
        private final String name;
        private final double value;

        BitUnit(String symbol, String name, double value) {
            this.symbol = symbol;
            this.name = name;
            this.value = value;
        }
    }

    public static String toBitSize(double bits) {
        final BitUnit[] units = BitUnit.values();

        for (int idx = units.length - 1; idx >= 0; idx--) {
            final BitUnit unit = units[idx];

            if (bits >= unit.value) {
                return String.format(DEC_UNIT, bits / unit.value, unit.symbol);
            }
        }
        return String.format(DEC_UNIT, bits, "bit");
    }

    public enum ByteUnit {
        K("K", "kilo", Math.pow(2, 10)),
        M("M", "mega", Math.pow(2, 20)),
        G("G", "giga", Math.pow(2, 30)),
        T("T", "tera", Math.pow(2, 40)),
        P("P", "peta", Math.pow(2, 50)),
        E("E", "exa", Math.pow(2, 60)),
        Z("Z", "zetta", Math.pow(2, 70)),
        Y("Y", "yotta", Math.pow(2, 80));

        private final String symbol;
        private final String name;
        private final double value;

        ByteUnit(String symbol, String name, double value) {
            this.symbol = symbol;
            this.name = name;
            this.value = value;
        }
    }

    public static String toByteSize(double bytes) {
        return toByteSize(bytes, true);
    }

    public static String toByteSize(double bytes, boolean withDecimal) {
        final ByteUnit[] units = ByteUnit.values();

        for (int idx = units.length - 1; idx >= 0; idx--) {
            final ByteUnit unit = units[idx];

            if (bytes >= unit.value) {
                if (withDecimal) {
                    return String.format(DEC_UNIT, bytes / unit.value, unit.symbol);
                }
                return String.format("%.0f %s", bytes / unit.value, unit.symbol);
            }
        }

        if (withDecimal) {
            return String.format(DEC_UNIT, bytes, "");
        }
        return String.format("%.0f %s", bytes, "");
    }

    /**
     * Converts a time in milli-seconds into ms, s, m, h or d.
     */
    public static String toElapsedTime(long millis) {
        double secs = millis / 1000.0;

        if (secs < 10.0) {
            return String.format("%dms", millis);
        } else if (secs < 60) {
            return String.format("%.2fs", secs);
        } else if (secs < 3600) {
            // noinspection NumericCastThatLosesPrecision
            return String.format("%dm%02ds", (int) Math.floor(secs / 60.0), Math.round(secs) % 60);
        } else if (secs < 24 * 3600) {
            // noinspection NumericCastThatLosesPrecision
            return String.format("%dh%02dm%02ds", (int) Math.floor(secs / 3600.0), (int) Math.floor(secs / 60.0) % 60, Math.round(secs) % 60);
        }
        // noinspection NumericCastThatLosesPrecision
        return String.format("%dd%02dh%02dm%02ds", (int) Math.floor(secs / 3600.0 / 24.0), (int) Math.floor(secs / 3600.0) % 24,
                (int) Math.floor(secs / 60.0) % 60, Math.round(secs) % 60);
    }

    /**
     * Converts a time in nano-seconds into ms, s, m, h or d.
     */
    public static String toElapsedNanoTime(long nanos) {
        if (nanos < 1000) {
            return String.format("%dns", nanos);
        }
        if (nanos < 1000 * 1000) {
            return String.format("%dus", nanos / 1000);
        }
        if (nanos < 1000 * 1000 * 1000) {
            return String.format("%dms", nanos / 1000 / 1000);
        }
        return toElapsedTime(nanos / 1000 / 1000);
    }

}
