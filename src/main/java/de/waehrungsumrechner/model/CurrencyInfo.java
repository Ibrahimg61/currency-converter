package de.waehrungsumrechner.model;

import java.util.Currency;
import java.util.Locale;

/** Währungscode mit deutschem Anzeigenamen, z. B. {@code USD – US-Dollar}. */
public record CurrencyInfo(String code, String name) {

    public static CurrencyInfo of(String code) {
        try {
            return new CurrencyInfo(code, Currency.getInstance(code).getDisplayName(Locale.GERMAN));
        } catch (IllegalArgumentException e) {
            // Von der API gelieferte Codes ohne ISO-4217-Eintrag (z. B. Kryptowährungen)
            return new CurrencyInfo(code, code);
        }
    }

    @Override
    public String toString() {
        return code + " " + name;
    }
}
