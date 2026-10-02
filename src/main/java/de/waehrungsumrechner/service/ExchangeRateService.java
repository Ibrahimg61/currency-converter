package de.waehrungsumrechner.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

import de.waehrungsumrechner.data.ExchangeRateException;
import de.waehrungsumrechner.data.ExchangeRateParser;
import de.waehrungsumrechner.data.RateSource;
import de.waehrungsumrechner.data.RateStore;
import de.waehrungsumrechner.model.ExchangeRates;
import de.waehrungsumrechner.model.RateHistory;

/** Verbindet Kursquelle und lokale Ablage: laden, speichern, auf Cache zurückfallen. */
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
     * Lädt die aktuellen Kurse und legt sie als Cache und Tageseintrag in der Historie ab.
     * Schlägt nur das Speichern fehl, werden die Kurse trotzdem geliefert.
     */
    public ExchangeRates refresh() throws ExchangeRateException {
        String json = source.fetchLatest();
        ExchangeRates rates = ExchangeRateParser.parse(json, ExchangeRates.Source.LIVE);
        try {
            store.saveLatest(json);
            store.recordDay(LocalDate.now(clock), rates.rates());
        } catch (ExchangeRateException e) {
            LOG.log(System.Logger.Level.WARNING, "Kurse konnten nicht gespeichert werden", e);
        }
        return rates;
    }

    /** Zuletzt gespeicherte Kurse; leer, wenn es keine gibt oder die Datei unbrauchbar ist. */
    public Optional<ExchangeRates> cachedRates() {
        try {
            Optional<String> json = store.loadLatest();
            if (json.isPresent()) {
                return Optional.of(ExchangeRateParser.parse(json.get(), ExchangeRates.Source.CACHE));
            }
        } catch (ExchangeRateException e) {
            LOG.log(System.Logger.Level.WARNING, "Gespeicherte Kurse sind unbrauchbar", e);
        }
        return Optional.empty();
    }

    public RateHistory history() {
        try {
            return store.loadHistory();
        } catch (ExchangeRateException e) {
            LOG.log(System.Logger.Level.WARNING, "Historie konnte nicht gelesen werden", e);
            return RateHistory.empty();
        }
    }
}
