package rateshift.data;

/** Source of the latest exchange rates in the JSON format of exchangerate-api. */
@FunctionalInterface
public interface RateSource {

    String fetchLatest() throws ExchangeRateException;
}
