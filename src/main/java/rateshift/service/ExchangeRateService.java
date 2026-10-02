package rateshift.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

import rateshift.data.ExchangeRateException;
import rateshift.data.ExchangeRateParser;
import rateshift.data.RateSource;
import rateshift.data.RateStore;
import rateshift.model.ExchangeRates;
import rateshift.model.RateHistory;

/** Connects the rate source and local storage: load, save, fall back to the cache. */
public final class ExchangeRateService {

    private static final System.Logger LOG = System.getLogger(ExchangeRateService.class.getName());

    private final RateSource source;
    private final RateStore store;
    private final Clock clock;

    public ExchangeRateService(RateSource source, RateStore store) {
        this(source, store, Clock.systemDefaultZone());
    }

    public ExchangeRateService(RateSource source, RateStore store, Clock clock) {
        this.source = source;
        this.store = store;
        this.clock = clock;
    }

    /**
     * Fetches the latest rates and stores them as the cache and as the day's history entry.
     * If only saving fails, the rates are still returned.
     */
    public ExchangeRates refresh() throws ExchangeRateException {
        String json = source.fetchLatest();
        ExchangeRates rates = ExchangeRateParser.parse(json, ExchangeRates.Source.LIVE);
        try {
            store.saveLatest(json);
            store.recordDay(LocalDate.now(clock), rates.rates());
        } catch (ExchangeRateException e) {
            LOG.log(System.Logger.Level.WARNING, "Exchange rates could not be saved", e);
        }
        return rates;
    }

    /** Last saved rates; empty if there are none or the file is unusable. */
    public Optional<ExchangeRates> cachedRates() {
        try {
            Optional<String> json = store.loadLatest();
            if (json.isPresent()) {
                return Optional.of(ExchangeRateParser.parse(json.get(), ExchangeRates.Source.CACHE));
            }
        } catch (ExchangeRateException e) {
            LOG.log(System.Logger.Level.WARNING, "Saved exchange rates are unusable", e);
        }
        return Optional.empty();
    }

    public RateHistory history() {
        try {
            return store.loadHistory();
        } catch (ExchangeRateException e) {
            LOG.log(System.Logger.Level.WARNING, "History could not be read", e);
            return RateHistory.empty();
        }
    }
}
