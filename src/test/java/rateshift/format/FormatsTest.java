package rateshift.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class FormatsTest {

    @Test
    void formatsLargeAmountsWithEnglishSeparators() {
        assertEquals("1,234.56", Formats.amount(1234.56));
    }

    @Test
    void showsAtLeastTwoDecimals() {
        assertEquals("5.00", Formats.amount(5));
    }

    @Test
    void showsMoreDecimalsForSmallValues() {
        assertEquals("0.0063", Formats.amount(0.0063));
    }

    @Test
    void formatsDate() {
        assertEquals("Mar 30, 2026", Formats.date(LocalDate.of(2026, 3, 30)));
    }
}
