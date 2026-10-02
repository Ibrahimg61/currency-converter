package de.waehrungsumrechner.format;

import java.util.OptionalDouble;
import java.util.regex.Pattern;

/** Liest Beträge in deutscher und englischer Schreibweise ({@code 1.234,56} / {@code 1,234.56}). */
public final class AmountParser {

    private static final Pattern DECIMAL = Pattern.compile("\\d+(\\.\\d*)?|\\.\\d+");

    private AmountParser() { }

    /** Leer bei fehlerhafter Eingabe oder negativem Betrag. */
    public static OptionalDouble parse(String input) {
        String s = input.strip().replace(" ", "");
        if (s.isEmpty()) {
            return OptionalDouble.empty();
        }

        int lastComma = s.lastIndexOf(',');
        int lastDot = s.lastIndexOf('.');
        if (lastComma >= 0 && lastDot >= 0) {
            // Das zuletzt stehende Zeichen ist das Dezimaltrennzeichen, das andere gruppiert.
            char decimal = lastComma > lastDot ? ',' : '.';
            char group = decimal == ',' ? '.' : ',';
            s = s.replace(String.valueOf(group), "").replace(decimal, '.');
        } else if (lastComma >= 0) {
            s = s.replace(',', '.');
        } else if (lastDot != s.indexOf('.')) {
            // Mehrere Punkte können nur Tausendertrenner sein: 1.234.567
            s = s.replace(".", "");
        }

        // Double.parseDouble akzeptiert auch "100f", "NaN" oder Hex-Notation – hier nur Ziffern erlaubt
        if (!DECIMAL.matcher(s).matches()) {
            return OptionalDouble.empty();
        }
        double value = Double.parseDouble(s);
        return Double.isFinite(value) ? OptionalDouble.of(value) : OptionalDouble.empty();
    }
}
