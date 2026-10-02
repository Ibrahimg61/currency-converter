package de.waehrungsumrechner.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

class RateHistoryTest {

    @Test
    void buildsChronologicalSeriesForPair() {
        TreeMap<LocalDate, Map<String, Double>> days = new TreeMap<>();
        days.put(LocalDate.of(2026, 3, 31), Map.of("EUR", 1.0, "USD", 1.5));
        days.put(LocalDate.of(2026, 3, 30), Map.of("EUR", 1.0, "USD", 1.25));

        List<RateHistory.Point> series = new RateHistory(days).series("EUR", "USD");

        assertEquals(List.of(
                new RateHistory.Point(LocalDate.of(2026, 3, 30), 1.25),
                new RateHistory.Point(LocalDate.of(2026, 3, 31), 1.5)), series);
    }

    @Test
    void skipsDaysWithoutBothCurrencies() {
        TreeMap<LocalDate, Map<String, Double>> days = new TreeMap<>();
        days.put(LocalDate.of(2026, 3, 30), Map.of("EUR", 1.0));

        assertTrue(new RateHistory(days).series("EUR", "USD").isEmpty());
    }
}
