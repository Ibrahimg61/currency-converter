package de.waehrungsumrechner.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class AmountParserTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "100|100.0",
            "12,5|12.5",
            "12.5|12.5",
            "1.234,56|1234.56",
            "1,234.56|1234.56",
            "1.234.567|1234567.0",
            " 7 |7.0",
            "0|0.0"})
    void parsesAmounts(String input, double expected) {
        assertEquals(expected, AmountParser.parse(input).orElseThrow(), 1e-9);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "1,2,3x", "-5", "NaN", "Infinity", "100f", "0x10", "1e5"})
    void rejectsInvalidInput(String input) {
        assertTrue(AmountParser.parse(input).isEmpty());
    }
}
