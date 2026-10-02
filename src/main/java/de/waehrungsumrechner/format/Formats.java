package de.waehrungsumrechner.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Deutsche Darstellung von Beträgen und Datumsangaben. */
public final class Formats {

    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.GERMANY);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Formats() { }

    /** Mindestens zwei Nachkommastellen; Werte unter 1 zeigen mehr, damit kleine Kurse lesbar bleiben. */
    public static String amount(double value) {
        String pattern = Math.abs(value) >= 1 || value == 0 ? "#,##0.00##" : "#,##0.0000####";
        // DecimalFormat ist nicht threadsicher, daher pro Aufruf eine Instanz
        return new DecimalFormat(pattern, SYMBOLS).format(value);
    }

    public static String date(LocalDate date) {
        return DATE.format(date);
    }
}
