package rateshift.model;

import java.util.Currency;
import java.util.Locale;

/** Currency code with its English display name, e.g. {@code USD US Dollar}. */
public record CurrencyInfo(String code, String name) {

    public static CurrencyInfo of(String code) {
        try {
            return new CurrencyInfo(code, Currency.getInstance(code).getDisplayName(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            // Codes returned by the API without an ISO 4217 entry (e.g. cryptocurrencies)
            return new CurrencyInfo(code, code);
        }
    }

    @Override
    public String toString() {
        return code + " " + name;
    }
}
