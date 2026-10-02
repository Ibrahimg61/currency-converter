# RateShift

Java Swing currency converter with live exchange rates from
[open.er-api.com](https://open.er-api.com/v6/latest/EUR) (base EUR, about 160 currencies).

## Features

- Live rates on startup and via the "Refresh" button
- Offline fallback to the last fetched rates
- Daily rates are stored as a history, which feeds a line chart for each currency pair
- Swap currencies, copy the result, enter amounts with `,` or `.` (both `1,234.56` and `1.234,56` work)
- Typing in the currency list jumps to a currency (e.g. `US` → USD)

## Requirements

Only a JDK 21 or newer. Maven does not need to be installed, the Maven Wrapper downloads it.

## Build and run

```bash
./mvnw verify                  # compile, run tests, build the jar
java -jar target/rateshift.jar
```

Tests only: `./mvnw test`. In VS Code or IntelliJ, open the project as a Maven project and run
`rateshift.Main`.

## Data and privacy

Rates and history are stored outside the repository in `~/.rateshift/`
(`latest.json` and `history.json`). Use a different location with
`java -Drateshift.data=/path -jar …`

The only network request is an anonymous `GET` to `open.er-api.com`. No API key, account or
personal data is involved, but the server naturally sees your IP address.

## Project layout

```
src/main/java/rateshift/
├── Main.java                 entry point, wires the parts together
├── model/                    plain data types and calculation logic, no I/O
│   ├── ExchangeRates         rates of one day, conversion
│   ├── RateHistory           daily rates, series per currency pair
│   └── CurrencyInfo          code + English name
├── data/                     rate source and storage
│   ├── RateSource            interface of the rate source
│   ├── HttpRateSource        fetches rates over HTTP
│   ├── ExchangeRateParser    JSON → ExchangeRates
│   └── RateStore             latest.json and history.json
├── service/
│   └── ExchangeRateService   load, save, fall back to the cache
├── format/                   parse input, format output (English)
└── ui/                       Swing user interface
    ├── MainWindow, ResultCard, HistoryCard, Theme
    └── component/            reusable building blocks (Card, FlatButton, CurrencyComboBox …)
```

Dependencies only point downwards: `ui` → `service` → `data` → `model`. The tests in
`src/test/java` cover the model, parser, storage, service and input/output formatting.

## License

Released under the [MIT License](LICENSE).

## Credits

Exchange rates by [Exchange Rate API](https://www.exchangerate-api.com)
(free endpoint `open.er-api.com`).
