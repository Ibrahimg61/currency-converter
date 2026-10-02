package rateshift.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rateshift.TestData;
import rateshift.data.ExchangeRateException;
import rateshift.data.RateStore;
import rateshift.model.ExchangeRates;

class ExchangeRateServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-04-01T10:00:00Z"), ZoneOffset.UTC);

    @TempDir
    Path dir;

    @Test
    void refreshReturnsLiveRatesAndPersistsThem() throws Exception {
        ExchangeRateService service = new ExchangeRateService(() -> TestData.API_RESPONSE, new RateStore(dir), CLOCK);

        ExchangeRates rates = service.refresh();

        assertEquals(ExchangeRates.Source.LIVE, rates.source());
        assertEquals(1, service.history().days().size());
        assertTrue(service.history().days().containsKey(LocalDate.of(2026, 4, 1)));
        assertEquals(ExchangeRates.Source.CACHE, service.cachedRates().orElseThrow().source());
    }

    @Test
    void refreshFailureLeavesCacheUntouched() throws Exception {
        RateStore store = new RateStore(dir);
        new ExchangeRateService(() -> TestData.API_RESPONSE, store, CLOCK).refresh();

        ExchangeRateService offline = new ExchangeRateService(() -> {
            throw new ExchangeRateException("offline");
        }, store, CLOCK);

        assertThrows(ExchangeRateException.class, offline::refresh);
        assertTrue(offline.cachedRates().isPresent());
    }

    @Test
    void cachedRatesAreEmptyWithoutCache() {
        ExchangeRateService service = new ExchangeRateService(() -> TestData.API_RESPONSE, new RateStore(dir), CLOCK);

        assertTrue(service.cachedRates().isEmpty());
    }
}
