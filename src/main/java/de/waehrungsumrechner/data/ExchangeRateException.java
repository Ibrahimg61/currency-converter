package de.waehrungsumrechner.data;

/** Kurse konnten nicht geladen, gelesen oder gespeichert werden. */
public class ExchangeRateException extends Exception {

    public ExchangeRateException(String message) {
        super(message);
    }

    public ExchangeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
