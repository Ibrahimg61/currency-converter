package de.waehrungsumrechner.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import de.waehrungsumrechner.model.RateHistory;

/**
 * Lokale Ablage in einem Datenverzeichnis: {@code latest.json} (zuletzt geladene API-Antwort,
 * Offline-Fallback) und {@code history.json} (ein Eintrag pro Tag).
 */
public final class RateStore {

    private static final int INDENT = 2;

    private final Path directory;
    private final Path latestFile;
    private final Path historyFile;

    public RateStore(Path directory) {
        this.directory = directory;
        this.latestFile = directory.resolve("latest.json");
        this.historyFile = directory.resolve("history.json");
    }

    public Optional<String> loadLatest() throws ExchangeRateException {
        return read(latestFile);
    }

    public void saveLatest(String json) throws ExchangeRateException {
        write(latestFile, json);
    }

    public RateHistory loadHistory() throws ExchangeRateException {
        Optional<String> content = read(historyFile);
        if (content.isEmpty()) {
            return RateHistory.empty();
        }
        try {
            Map<LocalDate, Map<String, Double>> days = new TreeMap<>();
            JSONObject json = new JSONObject(content.get());
            for (String date : json.keySet()) {
                JSONObject day = json.getJSONObject(date);
                Map<String, Double> rates = new TreeMap<>();
                for (String code : day.keySet()) {
                    rates.put(code, day.getDouble(code));
                }
                days.put(LocalDate.parse(date), rates);
            }
            return new RateHistory(new TreeMap<>(days));
        } catch (JSONException | DateTimeParseException e) {
            throw new ExchangeRateException("Historie ist beschädigt: " + historyFile, e);
        }
    }

    /** Speichert die Kurse für den Tag; ein vorhandener Eintrag desselben Tages wird ersetzt. */
    public void recordDay(LocalDate date, Map<String, Double> rates) throws ExchangeRateException {
        try {
            JSONObject history = read(historyFile).map(JSONObject::new).orElseGet(JSONObject::new);
            history.put(date.toString(), new JSONObject(rates));
            write(historyFile, history.toString(INDENT));
        } catch (JSONException e) {
            throw new ExchangeRateException("Historie ist beschädigt: " + historyFile, e);
        }
    }

    private Optional<String> read(Path file) throws ExchangeRateException {
        try {
            return Files.exists(file) ? Optional.of(Files.readString(file)) : Optional.empty();
        } catch (IOException e) {
            throw new ExchangeRateException("Datei nicht lesbar: " + file, e);
        }
    }

    /** Schreibt über eine Temp-Datei, damit ein Abbruch keine halbe Datei hinterlässt. */
    private void write(Path file, String content) throws ExchangeRateException {
        try {
            Files.createDirectories(directory);
            Path temp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(temp, content);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ExchangeRateException("Datei nicht schreibbar: " + file, e);
        }
    }
}
