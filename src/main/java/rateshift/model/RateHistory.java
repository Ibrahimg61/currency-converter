package rateshift.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/** Stored daily rates (date → currency → rate against the base currency). */
public record RateHistory(NavigableMap<LocalDate, Map<String, Double>> days) {

    /** Rate of the pair on one day. */
    public record Point(LocalDate date, double value) { }

    public RateHistory {
        days = Collections.unmodifiableNavigableMap(new TreeMap<>(days));
    }

    public static RateHistory empty() {
        return new RateHistory(new TreeMap<>());
    }

    /** Chronological series of 1 {@code from} in {@code to}; days missing either currency are skipped. */
    public List<Point> series(String from, String to) {
        List<Point> points = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Double>> day : days.entrySet()) {
            Double fromRate = day.getValue().get(from);
            Double toRate = day.getValue().get(to);
            if (fromRate != null && toRate != null && fromRate > 0) {
                points.add(new Point(day.getKey(), toRate / fromRate));
            }
        }
        return points;
    }
}
