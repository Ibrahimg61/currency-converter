# Währungsumrechner

Java-Swing-Programm zum Umrechnen von Währungen mit Live-Kursen von
[open.er-api.com](https://open.er-api.com/v6/latest/EUR) (Basis EUR, ca. 160 Währungen).

## Funktionen

- Live-Kurse beim Start und per Button „Aktualisieren“
- Offline-Fallback auf die zuletzt geladenen Kurse
- Tageskurse werden als Historie gespeichert; daraus entsteht ein Verlaufsdiagramm je Währungspaar
- Währungen tauschen, Ergebnis kopieren, Eingabe mit `,` oder `.` (`1.234,56` und `1,234.56` funktionieren)
- Tippen in der Auswahlliste springt zur Währung (z. B. `US` → USD)

## Voraussetzungen

Nur ein JDK ab Version 21. Maven muss nicht installiert sein, der Maven Wrapper lädt es selbst.

## Bauen und starten

```bash
./mvnw verify                          # kompilieren, Tests ausführen, Jar bauen
java -jar target/waehrungsumrechner.jar
```

Nur testen: `./mvnw test`. In VS Code oder IntelliJ das Projekt als Maven-Projekt öffnen und
`de.waehrungsumrechner.Main` starten.

## Daten

Kurse und Historie liegen außerhalb des Repos in `~/.waehrungsumrechner/`
(`latest.json` und `history.json`). Anderer Ort: `java -Dwaehrungsumrechner.data=/pfad -jar …`

## Aufbau

```
src/main/java/de/waehrungsumrechner/
├── Main.java                 Einstiegspunkt, verdrahtet die Teile
├── model/                    reine Datentypen und Rechenlogik, kein I/O
│   ├── ExchangeRates         Kurse eines Tages, Umrechnung
│   ├── RateHistory           Tageskurse, Verlauf je Währungspaar
│   └── CurrencyInfo          Code + deutscher Name
├── data/                     Kursquelle und Speicherung
│   ├── RateSource            Schnittstelle der Kursquelle
│   ├── HttpRateSource        Abruf per HTTP
│   ├── ExchangeRateParser    JSON → ExchangeRates
│   └── RateStore             latest.json und history.json
├── service/
│   └── ExchangeRateService   laden, speichern, Cache-Fallback
├── format/                   Eingabe lesen, Ausgabe formatieren (Deutsch)
└── ui/                       Swing-Oberfläche
    ├── MainWindow, ResultCard, HistoryCard, Theme
    └── component/            wiederverwendbare Bausteine (Card, FlatButton, CurrencyComboBox …)
```

Abhängigkeiten zeigen nur nach unten: `ui` → `service` → `data` → `model`. Die Tests in
`src/test/java` decken Modell, Parser, Speicher, Service und Eingabe-/Ausgabeformat ab.
