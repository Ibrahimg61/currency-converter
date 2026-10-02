package rateshift.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ExchangeRatesTest {

    private final ExchangeRates rates = new ExchangeRates("EUR",
            Map.of("EUR", 1.0, "USD", 1.25, "CHF", 0.5),
            LocalDate.of(2026, 3, 30), ExchangeRates.Source.LIVE);

    @Test
    void convertsFromBaseCurrency() {
        assertEquals(125.0, rates.convert(100, "EUR", "USD"), 1e-9);
    }

    @Test
    void convertsToBaseCurrency() {
        assertEquals(80.0, rates.convert(100, "USD", "EUR"), 1e-9);
    }

    @Test
    void convertsBetweenTwoNonBaseCurrencies() {
        assertEquals(250.0, rates.convert(100, "CHF", "USD"), 1e-9);
    }

    @Test
    void sameCurrencyIsIdentity() {
        assertEquals(42.0, rates.convert(42, "USD", "USD"), 1e-9);
    }

    @Test
    void listsCodesAlphabetically() {
        assertEquals(List.of("CHF", "EUR", "USD"), List.copyOf(rates.currencyCodes()));
    }

    @Test
    void rejectsUnknownCurrency() {
        assertThrows(IllegalArgumentException.class, () -> rates.convert(1, "EUR", "XXX"));
    }

    @Test
    void rejectsNonPositiveRates() {
        assertThrows(IllegalArgumentException.class, () -> new ExchangeRates("EUR",
                Map.of("EUR", 1.0, "USD", 0.0), LocalDate.now(), ExchangeRates.Source.LIVE));
    }
}
