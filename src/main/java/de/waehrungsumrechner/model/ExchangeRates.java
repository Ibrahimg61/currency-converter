package de.waehrungsumrechner.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * Wechselkurse zu einem Stichtag. Alle Kurse beziehen sich auf die Basiswährung
 * ({@code rates.get(base) == 1}); Umrechnungen zwischen beliebigen Paaren laufen darüber.
 */
public record ExchangeRates(String base, Map<String, Double> rates, LocalDate lastUpdate, Source source) {

    /** Herkunft der Kurse. */
    public enum Source { LIVE, CACHE }

    public ExchangeRates {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(lastUpdate, "lastUpdate");
        Objects.requireNonNull(source, "source");
        for (Map.Entry<String, Double> e : rates.entrySet()) {
            if (!(e.getValue() > 0) || Double.isInfinite(e.getValue())) {
                throw new IllegalArgumentException("Ungültiger Kurs für " + e.getKey() + ": " + e.getValue());
            }
        }
        rates = Collections.unmodifiableSortedMap(new TreeMap<>(rates));
    }

    /** Währungscodes in alphabetischer Reihenfolge. */
    public Set<String> currencyCodes() {
        return rates.keySet();
    }

    /** Kurs für 1 {@code from} in {@code to}. */
    public double rate(String from, String to) {
        return rateOf(to) / rateOf(from);
    }

    public double convert(double amount, String from, String to) {
        return amount * rate(from, to);
    }

    private double rateOf(String code) {
        Double rate = rates.get(code);
        if (rate == null) {
            throw new IllegalArgumentException("Unbekannte Währung: " + code);
        }
        return rate;
    }
}
