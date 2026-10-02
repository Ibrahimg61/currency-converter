package rateshift.format;

import java.util.OptionalDouble;
import java.util.regex.Pattern;

/** Reads amounts in English and German notation ({@code 1.234,56} / {@code 1,234.56}). */
public final class AmountParser {

    private static final Pattern DECIMAL = Pattern.compile("\\d+(\\.\\d*)?|\\.\\d+");

    private AmountParser() { }

    /** Empty for invalid input or a negative amount. */
    public static OptionalDouble parse(String input) {
        String s = input.strip().replace(" ", "");
        if (s.isEmpty()) {
            return OptionalDouble.empty();
        }

        int lastComma = s.lastIndexOf(',');
        int lastDot = s.lastIndexOf('.');
        if (lastComma >= 0 && lastDot >= 0) {
            // The last separator is the decimal separator, the other one groups digits.
            char decimal = lastComma > lastDot ? ',' : '.';
            char group = decimal == ',' ? '.' : ',';
            s = s.replace(String.valueOf(group), "").replace(decimal, '.');
        } else if (lastComma >= 0) {
            s = s.replace(',', '.');
        } else if (lastDot != s.indexOf('.')) {
            // Several dots can only be thousands separators: 1.234.567
            s = s.replace(".", "");
        }

        // Double.parseDouble also accepts "100f", "NaN" or hex notation – only digits are allowed here
        if (!DECIMAL.matcher(s).matches()) {
            return OptionalDouble.empty();
        }
        double value = Double.parseDouble(s);
        return Double.isFinite(value) ? OptionalDouble.of(value) : OptionalDouble.empty();
    }
}
