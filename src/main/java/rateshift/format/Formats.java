package rateshift.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** English formatting of amounts and dates. */
public final class Formats {

    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

    private Formats() { }

    /** At least two decimal places; values below 1 show more so small rates stay readable. */
    public static String amount(double value) {
        String pattern = Math.abs(value) >= 1 || value == 0 ? "#,##0.00##" : "#,##0.0000####";
        // DecimalFormat is not thread-safe, so create one instance per call
        return new DecimalFormat(pattern, SYMBOLS).format(value);
    }

    public static String date(LocalDate date) {
        return DATE.format(date);
    }
}
