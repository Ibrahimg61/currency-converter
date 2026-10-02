package rateshift.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import rateshift.model.ExchangeRates;

/** Converts the API's JSON response into {@link ExchangeRates}. */
public final class ExchangeRateParser {

    private ExchangeRateParser() { }

    public static ExchangeRates parse(String json, ExchangeRates.Source source) throws ExchangeRateException {
        try {
            JSONObject root = new JSONObject(json);
            if (!"success".equals(root.optString("result"))) {
                throw new ExchangeRateException("The API did not report success");
            }
            JSONObject ratesJson = root.getJSONObject("rates");
            Map<String, Double> rates = new HashMap<>();
            for (String code : ratesJson.keySet()) {
                rates.put(code, ratesJson.getDouble(code));
            }
            LocalDate lastUpdate = Instant.ofEpochSecond(root.getLong("time_last_update_unix"))
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();
            return new ExchangeRates(root.getString("base_code"), rates, lastUpdate, source);
        } catch (JSONException | IllegalArgumentException e) {
            throw new ExchangeRateException("Invalid exchange rate data: " + e.getMessage(), e);
        }
    }
}
