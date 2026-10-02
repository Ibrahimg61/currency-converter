package de.waehrungsumrechner.data;

/** Quelle der aktuellen Kurse im JSON-Format der exchangerate-api. */
@FunctionalInterface
public interface RateSource {

    String fetchLatest() throws ExchangeRateException;
}
