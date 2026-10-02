package de.waehrungsumrechner.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.waehrungsumrechner.model.RateHistory;

class RateStoreTest {

    @TempDir
    Path dir;

    @Test
    void latestIsEmptyBeforeFirstSave() throws Exception {
        assertTrue(new RateStore(dir).loadLatest().isEmpty());
    }

    @Test
    void savesAndLoadsLatest() throws Exception {
        RateStore store = new RateStore(dir.resolve("nested"));
        store.saveLatest("{\"a\":1}");

        assertEquals("{\"a\":1}", store.loadLatest().orElseThrow());
    }

    @Test
    void historyIsEmptyBeforeFirstEntry() throws Exception {
        assertTrue(new RateStore(dir).loadHistory().days().isEmpty());
    }

    @Test
    void recordsOneEntryPerDayAndReplacesSameDay() throws Exception {
        RateStore store = new RateStore(dir);
        store.recordDay(LocalDate.of(2026, 3, 30), Map.of("EUR", 1.0, "USD", 1.2));
        store.recordDay(LocalDate.of(2026, 3, 31), Map.of("EUR", 1.0, "USD", 1.3));
        store.recordDay(LocalDate.of(2026, 3, 31), Map.of("EUR", 1.0, "USD", 1.4));

        RateHistory history = store.loadHistory();

        assertEquals(2, history.days().size());
        assertEquals(1.2, history.days().get(LocalDate.of(2026, 3, 30)).get("USD"));
        assertEquals(1.4, history.days().get(LocalDate.of(2026, 3, 31)).get("USD"));
    }

    @Test
    void reportsCorruptHistory() throws Exception {
        Files.writeString(dir.resolve("history.json"), "not json");

        assertThrows(ExchangeRateException.class, () -> new RateStore(dir).loadHistory());
    }
}
