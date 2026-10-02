package rateshift.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import rateshift.TestData;
import rateshift.model.ExchangeRates;

class ExchangeRateParserTest {

    @Test
    void parsesApiResponse() throws Exception {
        ExchangeRates rates = ExchangeRateParser.parse(TestData.API_RESPONSE, ExchangeRates.Source.LIVE);

        assertEquals("EUR", rates.base());
        assertEquals(LocalDate.of(2026, 3, 30), rates.lastUpdate());
        assertEquals(1.25, rates.rates().get("USD"));
        assertEquals(ExchangeRates.Source.LIVE, rates.source());
    }

    @Test
    void rejectsErrorResponse() {
        assertThrows(ExchangeRateException.class, () ->
                ExchangeRateParser.parse("{\"result\":\"error\"}", ExchangeRates.Source.LIVE));
    }

    @Test
    void rejectsGarbage() {
        assertThrows(ExchangeRateException.class, () ->
                ExchangeRateParser.parse("<html>nope</html>", ExchangeRates.Source.LIVE));
    }

    @Test
    void rejectsMissingRates() {
        assertThrows(ExchangeRateException.class, () -> ExchangeRateParser.parse(
                "{\"result\":\"success\",\"base_code\":\"EUR\",\"time_last_update_unix\":1}",
                ExchangeRates.Source.LIVE));
    }
}
