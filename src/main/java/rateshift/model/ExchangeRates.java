package rateshift.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * Exchange rates for a given day. All rates refer to the base currency
 * ({@code rates.get(base) == 1}); conversions between any pair go through it.
 */
public record ExchangeRates(String base, Map<String, Double> rates, LocalDate lastUpdate, Source source) {

    /** Where the rates come from. */
    public enum Source { LIVE, CACHE }

    public ExchangeRates {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(lastUpdate, "lastUpdate");
        Objects.requireNonNull(source, "source");
        for (Map.Entry<String, Double> e : rates.entrySet()) {
            if (!(e.getValue() > 0) || Double.isInfinite(e.getValue())) {
                throw new IllegalArgumentException("Invalid rate for " + e.getKey() + ": " + e.getValue());
            }
        }
        rates = Collections.unmodifiableSortedMap(new TreeMap<>(rates));
    }

    /** Currency codes in alphabetical order. */
    public Set<String> currencyCodes() {
        return rates.keySet();
    }

    /** Rate for 1 {@code from} in {@code to}. */
    public double rate(String from, String to) {
        return rateOf(to) / rateOf(from);
    }

    public double convert(double amount, String from, String to) {
        return amount * rate(from, to);
    }

    private double rateOf(String code) {
        Double rate = rates.get(code);
        if (rate == null) {
            throw new IllegalArgumentException("Unknown currency: " + code);
        }
        return rate;
    }
}
