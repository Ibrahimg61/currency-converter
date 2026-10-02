package rateshift;

/** Shared test data in the API's format. */
public final class TestData {

    /** As of 2026-03-30 (UTC). */
    public static final String API_RESPONSE = """
            {"result":"success","base_code":"EUR","time_last_update_unix":1774828951,
             "rates":{"EUR":1,"USD":1.25,"CHF":0.5,"JPY":160}}
            """;

    private TestData() { }
}
